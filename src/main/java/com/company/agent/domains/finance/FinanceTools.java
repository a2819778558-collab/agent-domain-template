package com.company.agent.domains.finance;

import com.company.agent.core.spi.ToolSpec;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 财务域的工具集 ——「业务动词」写法的完整示例。
 *
 * <p>对比一下两种写法就明白差别了：
 * <pre>
 *   ❌ updatePolicyField(policyId, field, value)   // 模型自己拼业务逻辑，规则拦不住
 *   ✅ finance.surrender / service.tryCashValue     // 一个工具 = 一个业务动作
 * </pre>
 *
 * <p>下面返回的是假数据，仅用于演示工具「长什么样」。真实实现里换成 service / mapper 调用。
 */
public final class FinanceTools {

    private FinanceTools() {
    }

    public static List<ToolSpec> all() {
        return List.of(queryArrears(), calcLateFee(), queryReimbursement());
    }

    /** 查询欠费明细 —— 只读，参数可以粗一点。 */
    static ToolSpec queryArrears() {
        return ToolSpec.of(
                "finance.queryArrears",
                "查询指定保单的欠费明细（欠费期数、金额、状态）。用户问「有没有欠费 / 欠多少」时使用。",
                List.of(ToolSpec.ParamSpec.required("policyNo", "string", "保单号")),
                args -> {
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("policyNo", args.get("policyNo"));
                    result.put("欠费期数", 2);
                    result.put("欠费金额", 3860.00);
                    result.put("状态", "已逾期");
                    return result;
                });
    }

    /** 计算滞纳金/利息。 */
    static ToolSpec calcLateFee() {
        return ToolSpec.of(
                "finance.calcLateFee",
                "根据保单号计算当前欠费对应的滞纳金与利息。用户问「滞纳金 / 利息怎么算」时使用。",
                List.of(ToolSpec.ParamSpec.required("policyNo", "string", "保单号")),
                args -> {
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("policyNo", args.get("policyNo"));
                    result.put("滞纳金", 115.80);
                    result.put("利息", 42.15);
                    result.put("计费截止日", "2026-09-30");
                    return result;
                });
    }

    /** 查询费用报销状态。 */
    static ToolSpec queryReimbursement() {
        return ToolSpec.of(
                "finance.queryReimbursement",
                "根据报销单号查询费用报销进度。用户问「报销到哪了 / 发票报没报」时使用。",
                List.of(ToolSpec.ParamSpec.required("orderNo", "string", "报销单号")),
                args -> {
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("orderNo", args.get("orderNo"));
                    result.put("状态", "审核中");
                    result.put("当前节点", "财务复核");
                    return result;
                });
    }
}
