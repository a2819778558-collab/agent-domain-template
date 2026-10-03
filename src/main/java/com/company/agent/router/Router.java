package com.company.agent.router;

/**
 * 路由器契约。
 *
 * <p>实现建议走「级联」：
 * <pre>
 *   L0 规则/关键词       命中高确定性词 → 直接定（可解释性最强）
 *   L1 向量语义路由      top1 相似度明显领先 → 直通
 *   L2 LLM 结构化分类    处理模糊 / 多意图 / 低置信度
 *   L3 澄清 / 转人工      置信度低于阈值
 * </pre>
 *
 * <p>无论哪一层，输出都必须是受约束的（域用 enum / 白名单），而不是自由文本。
 */
@FunctionalInterface
public interface Router {

    RouteResult route(String userQuery);
}
