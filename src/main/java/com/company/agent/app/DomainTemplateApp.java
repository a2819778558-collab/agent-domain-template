package com.company.agent.app;

import com.company.agent.core.DomainRegistry;
import com.company.agent.core.DomainToolAssembler;
import com.company.agent.core.spi.DomainPlugin;
import com.company.agent.core.spi.ToolSpec;
import com.company.agent.domains.finance.FinanceDomain;
import com.company.agent.domains.protection.ProtectionDomain;
import com.company.agent.domains.renewal.RenewalDomain;
import com.company.agent.domains.servicing.ServicingDomain;
import com.company.agent.router.KeywordRouter;
import com.company.agent.router.RouteResult;
import com.company.agent.router.Router;

import java.util.List;
import java.util.Map;

/**
 * 演示入口：跑一遍「注册域 → 路由 → 装配多域工具 → 跨域取数」的全流程。
 *
 * <p>运行：{@code mvn -q compile exec:java}
 *
 * <p>想看的重点：第一条问题会<b>同时命中 renewal / servicing / finance 三个域</b>，
 * 装配出来的是这三个域工具的<b>并集</b>——这就是跨域问题不会被单域困住的证据。
 */
public class DomainTemplateApp {

    public static void main(String[] args) {
        // 1. 平台启动：注册全部域
        //    （接入 Spring 后，这一步变成 DomainRegistry 构造器注入 List<DomainPlugin>，自动收集）
        DomainRegistry registry = new DomainRegistry()
                .register(new FinanceDomain())
                .register(new RenewalDomain())
                .register(new ServicingDomain())
                .register(new ProtectionDomain());

        System.out.println("已注册的域：");
        registry.all().forEach(d -> System.out.printf(
                "  - %-10s %-6s  意图 %d 个, 工具 %d 个%n",
                d.name(), d.displayName(), d.intents().size(), d.tools().size()));

        // 2. 路由（这里用最简关键词路由；真实项目换成 规则+向量+LLM 级联）
        Router router = new KeywordRouter();

        String[] queries = {
                "我这保单下个月到期，想用现金价值续一下，顺便看看有没有欠费",
                "报销单 R20260901 到哪一步了",
                "我想投诉，怎么弄"
        };

        for (String query : queries) {
            handle(registry, router, query);
        }
    }

    private static void handle(DomainRegistry registry, Router router, String query) {
        System.out.println("\n================ 用户：" + query);

        RouteResult route = router.route(query);
        System.out.printf("路由结果：domains=%s, primary=%s, confidence=%.2f, 需澄清=%s%n",
                route.domains(), route.primaryDomain(), route.confidence(), route.needsClarification());
        System.out.println("决策原因：" + route.reason() + "   (→ 写审计日志)");

        if (route.needsClarification()) {
            System.out.println("→ 置信度/命中不足，先向用户澄清，不装配工具");
            return;
        }

        // 3. 装配「涉及域」的工具并集
        List<DomainPlugin> domains = registry.resolve(route.domains());
        List<ToolSpec> tools = DomainToolAssembler.assemble(domains);
        System.out.println("装配工具(" + tools.size() + ")："
                + tools.stream().map(ToolSpec::name).toList());

        // 4. 模拟 agent loop 跨域取数（真实项目这里由 LLM 决定调用哪些工具）
        System.out.println("→ 模拟跨域取数：");
        for (DomainPlugin domain : domains) {
            ToolSpec tool = domain.tools().get(0);
            String firstParam = tool.parameters().get(0).name();
            Map<String, Object> arg = Map.of(firstParam, "P12345678");
            System.out.printf("   [%s] %s => %s%n", domain.displayName(), tool.name(), tool.invoke(arg));
        }
    }
}
