package com.company.agent.domains.renewal;

import com.company.agent.core.Resources;
import com.company.agent.core.spi.DomainPlugin;
import com.company.agent.core.spi.FallbackPolicy;
import com.company.agent.core.spi.IntentSpec;
import com.company.agent.core.spi.ToolSpec;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 续期域 —— 骨架模板（照 FinanceDomain 抄）。
 *
 * <p>TODO 接入时补齐：
 * <ol>
 *   <li>找续期组要真实的意图清单（或从工单里提取）；</li>
 *   <li>把下面的示例工具换成真实 service 调用；</li>
 *   <li>写 resources/renewal/system.md 与 renewal/slots.yml。</li>
 * </ol>
 *
 * <p>注意：续期天然牵扯财务（欠费、滞纳金），所以跨域是常态，
 * 不要因为这个域「不纯」就把它和财务合并——域是管理单位，不是执行牢笼。
 */
public class RenewalDomain implements DomainPlugin {

    @Override
    public String name() {
        return "renewal";
    }

    @Override
    public String displayName() {
        return "续期";
    }

    @Override
    public List<IntentSpec> intents() {
        return List.of(
                IntentSpec.of("renewal.status", "查询保单续期状态",
                        "我的保单什么时候到期", "续期成功了吗", "下一期什么时候扣"),
                IntentSpec.of("renewal.premium", "计算续期应缴保费",
                        "这一期要交多少", "续期保费是多少", "换成月交多少钱")
        );
    }

    @Override
    public List<ToolSpec> tools() {
        return List.of(queryStatus(), calcPremium());
    }

    @Override
    public String systemPrompt() {
        return Resources.load("renewal/system.md", "你是续期助手。");
    }

    @Override
    public FallbackPolicy fallback() {
        return FallbackPolicy.toHuman("续期组");
    }

    // --- 工具（业务动词） ---

    static ToolSpec queryStatus() {
        return ToolSpec.of(
                "renewal.queryStatus",
                "查询指定保单的续期状态（到期日、应缴状态、宽限期）。",
                List.of(ToolSpec.ParamSpec.required("policyNo", "string", "保单号")),
                args -> {
                    Map<String, Object> r = new LinkedHashMap<>();
                    r.put("policyNo", args.get("policyNo"));
                    r.put("下期到期日", "2026-10-20");
                    r.put("宽限期至", "2026-12-20");
                    r.put("续期状态", "待缴费");
                    return r;
                });
    }

    static ToolSpec calcPremium() {
        return ToolSpec.of(
                "renewal.calcPremium",
                "计算续期应缴保费。可指定缴费方式（年交/月交/季交）。",
                List.of(
                        ToolSpec.ParamSpec.required("policyNo", "string", "保单号"),
                        ToolSpec.ParamSpec.optional("payMode", "string", "缴费方式：年交/月交/季交")),
                args -> {
                    Map<String, Object> r = new LinkedHashMap<>();
                    r.put("policyNo", args.get("policyNo"));
                    r.put("缴费方式", args.getOrDefault("payMode", "年交"));
                    r.put("应缴保费", 6800.00);
                    return r;
                });
    }
}
