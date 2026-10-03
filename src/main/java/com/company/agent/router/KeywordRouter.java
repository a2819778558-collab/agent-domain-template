package com.company.agent.router;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 演示用的最简路由器：只做 L0 关键词命中。
 *
 * <p>真实项目里应替换为「规则 + 向量 + LLM 结构化分类」的级联实现。
 * 这里保留它，是为了让「路由输出是一个域集合」这件事一眼可见——
 * 跑一下 {@code DomainTemplateApp} 就能看到一条跨域问题同时命中三个域。
 */
public class KeywordRouter implements Router {

    /** 每个域的关键词。真实项目里这些词来自意图表(intents) 的 examples。 */
    private static final Map<String, List<String>> KEYWORDS = Map.of(
            "renewal",    List.of("续期", "到期", "续保", "缴费", "保费"),
            "servicing",  List.of("保全", "退保", "现金价值", "受益人", "保单贷款", "犹豫期"),
            "finance",    List.of("欠费", "利息", "滞纳金", "报销", "费用", "发票", "账户"),
            "protection", List.of("投诉", "消保", "消费者", "维权", "纠纷", "回访")
    );

    @Override
    public RouteResult route(String userQuery) {
        Set<String> hits = new LinkedHashSet<>();
        for (Map.Entry<String, List<String>> entry : KEYWORDS.entrySet()) {
            if (entry.getValue().stream().anyMatch(userQuery::contains)) {
                hits.add(entry.getKey());
            }
        }

        if (hits.isEmpty()) {
            return RouteResult.askClarification("未命中任何域关键词，需要向用户澄清");
        }

        String primary = hits.iterator().next();
        double confidence = Math.min(1.0, 0.4 + 0.2 * hits.size());
        return new RouteResult(hits, primary, confidence, false, "关键词命中: " + hits);
    }
}
