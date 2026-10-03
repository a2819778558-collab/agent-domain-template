package com.company.agent.domains.protection;

import com.company.agent.core.Resources;
import com.company.agent.core.spi.DomainPlugin;
import com.company.agent.core.spi.FallbackPolicy;
import com.company.agent.core.spi.IntentSpec;
import com.company.agent.core.spi.ToolSpec;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 消费者保护域 —— 骨架模板。
 *
 * <p>这个域和另外三个不太一样：它有大量「开放式知识问答」（消保政策、条款解释），
 * 那属于 RAG，不属于工具调用。所以它的 intent 里会有相当一部分 task_type = rag。
 *
 * <p>提示：路由层要能区分「工具型 / 知识型 / 闲聊 / 转人工」，
 * 否则会拿工具去硬答知识问题。
 */
public class ProtectionDomain implements DomainPlugin {

    @Override
    public String name() {
        return "protection";
    }

    @Override
    public String displayName() {
        return "消费者保护";
    }

    @Override
    public List<IntentSpec> intents() {
        return List.of(
                IntentSpec.of("protection.complaint", "查询投诉处理进度",
                        "我的投诉处理到哪了", "投诉什么时候有结果"),
                IntentSpec.of("protection.policy", "查询消保政策与条款解释",
                        "犹豫期是多少天", "消保对退保有啥规定")
        );
    }

    @Override
    public List<ToolSpec> tools() {
        return List.of(queryComplaint());
    }

    @Override
    public String systemPrompt() {
        return Resources.load("protection/system.md", "你是消费者保护助手。");
    }

    @Override
    public FallbackPolicy fallback() {
        return FallbackPolicy.toHuman("消保组");
    }

    static ToolSpec queryComplaint() {
        return ToolSpec.of(
                "protection.queryComplaint",
                "根据工单号查询投诉处理进度。",
                List.of(ToolSpec.ParamSpec.required("caseNo", "string", "投诉工单号")),
                args -> {
                    Map<String, Object> r = new LinkedHashMap<>();
                    r.put("caseNo", args.get("caseNo"));
                    r.put("状态", "处理中");
                    r.put("当前部门", "消保部");
                    r.put("承诺办结", "2026-10-10");
                    return r;
                });
    }
}
