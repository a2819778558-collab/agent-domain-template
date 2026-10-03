package com.company.agent.router;

import java.util.Set;

/**
 * 路由结果。
 *
 * <p>关键：{@code domains} 是<b>集合</b>，不是单个。跨域问题就是要能一次命中多个域。
 *
 * @param domains           本次问题涉及的所有域（可能多个）
 * @param primaryDomain     主域，用于确定对话风格 / 兜底归属 / 权限起点
 * @param confidence        置信度，低于阈值应触发澄清
 * @param needsClarification 是否需要先反问用户
 * @param reason            决策原因（写进审计日志，保险场景要可解释）
 */
public record RouteResult(Set<String> domains,
                          String primaryDomain,
                          double confidence,
                          boolean needsClarification,
                          String reason) {

    public static RouteResult askClarification(String reason) {
        return new RouteResult(Set.of(), null, 0.0, true, reason);
    }
}
