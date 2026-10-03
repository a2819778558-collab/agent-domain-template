package com.company.agent.domains.finance;

import com.company.agent.core.Resources;
import com.company.agent.core.spi.DomainPlugin;
import com.company.agent.core.spi.FallbackPolicy;
import com.company.agent.core.spi.IntentSpec;
import com.company.agent.core.spi.PermissionPolicy;
import com.company.agent.core.spi.ToolSpec;

import java.util.List;

/**
 * 财务域。
 *
 * <p>这是「一个域完整长什么样」的样板。其他三个域（renewal / servicing / protection）
 * 就是照着这个复制，然后填自己的意图表、工具、提示词。
 *
 * <p>接入 Spring 时，类上加 {@code @Component} 即可被 DomainRegistry 自动收集。
 */
public class FinanceDomain implements DomainPlugin {

    @Override
    public String name() {
        return "finance";
    }

    @Override
    public String displayName() {
        return "财务";
    }

    @Override
    public List<IntentSpec> intents() {
        return List.of(
                IntentSpec.of("finance.arrears", "查询保单欠费明细",
                        "我这张保单有没有欠费", "欠了多少钱", "还剩几期没交"),
                IntentSpec.of("finance.lateFee", "计算滞纳金与利息",
                        "滞纳金怎么算", "利息是多少", "逾期要交多少"),
                IntentSpec.of("finance.reimbursement", "查询费用报销进度",
                        "报销到哪一步了", "发票报没报", "报销单状态")
        );
    }

    @Override
    public List<ToolSpec> tools() {
        return FinanceTools.all();
    }

    @Override
    public String systemPrompt() {
        return Resources.load("finance/system.md", "你是财务助手。");
    }

    @Override
    public PermissionPolicy permissions() {
        // 财务数据敏感：只允许访问 finance. 前缀的工具。
        return PermissionPolicy.allowPrefix("finance.");
    }

    @Override
    public FallbackPolicy fallback() {
        return FallbackPolicy.toHuman("财务组");
    }
}
