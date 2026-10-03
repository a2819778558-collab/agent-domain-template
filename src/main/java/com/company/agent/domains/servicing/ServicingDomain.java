package com.company.agent.domains.servicing;

import com.company.agent.core.Resources;
import com.company.agent.core.spi.DomainPlugin;
import com.company.agent.core.spi.FallbackPolicy;
import com.company.agent.core.spi.IntentSpec;
import com.company.agent.core.spi.ToolSpec;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 保全域 —— 骨架模板。
 *
 * <p>保全多为「写操作」（退保、变更受益人、保单贷款），所以这里的工具尤其要遵守
 * 「业务动词 + 规则内聚」原则：犹豫期校验、现金价值计算等业务规则都应封装在工具内部，
 * 不要丢给模型去拼。
 */
public class ServicingDomain implements DomainPlugin {

    @Override
    public String name() {
        return "servicing";
    }

    @Override
    public String displayName() {
        return "保全";
    }

    @Override
    public List<IntentSpec> intents() {
        return List.of(
                IntentSpec.of("servicing.cashValue", "试算保单现金价值",
                        "退保能拿回多少钱", "现金价值是多少"),
                IntentSpec.of("servicing.surrender", "办理犹豫期退保",
                        "我要退保", "犹豫期退保怎么弄"),
                IntentSpec.of("servicing.beneficiary", "变更受益人",
                        "我要改受益人", "受益人怎么改")
        );
    }

    @Override
    public List<ToolSpec> tools() {
        return List.of(tryCashValue(), surrender());
    }

    @Override
    public String systemPrompt() {
        return Resources.load("servicing/system.md", "你是保全助手。");
    }

    @Override
    public FallbackPolicy fallback() {
        return FallbackPolicy.toHuman("保全组");
    }

    // --- 工具（业务动词） ---

    static ToolSpec tryCashValue() {
        return ToolSpec.of(
                "servicing.tryCashValue",
                "试算指定保单当前可领取的现金价值（只读，不做任何变更）。",
                List.of(ToolSpec.ParamSpec.required("policyNo", "string", "保单号")),
                args -> {
                    Map<String, Object> r = new LinkedHashMap<>();
                    r.put("policyNo", args.get("policyNo"));
                    r.put("现金价值", 12450.30);
                    r.put("试算日", "2026-10-01");
                    return r;
                });
    }

    static ToolSpec surrender() {
        return ToolSpec.of(
                "servicing.surrender",
                "为指定保单办理犹豫期退保。工具内部会自行校验是否在犹豫期内、计算退还金额。"
                        + "只有在用户明确要求退保、且已确认保单号后才调用。",
                List.of(ToolSpec.ParamSpec.required("policyNo", "string", "保单号")),
                args -> {
                    Map<String, Object> r = new LinkedHashMap<>();
                    r.put("policyNo", args.get("policyNo"));
                    r.put("受理结果", "已受理");
                    r.put("退还金额", 5000.00);
                    r.put("预计到账", "T+3 工作日");
                    return r;
                });
    }
}
