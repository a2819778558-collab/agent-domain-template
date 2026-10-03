package com.company.agent.core;

import com.company.agent.core.spi.DomainPlugin;
import com.company.agent.core.spi.ToolSpec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 工具装配器 —— 把「本次命中的若干个域」的工具拼成并集，交给 agent loop。
 *
 * <p>这一步是跨域能力的核心：
 * <b>一次对话能调多少工具，由「这个问题涉及哪些域」决定，而不是「它属于哪个域」。</b>
 *
 * <pre>
 *   用户："保单下个月到期，想用现金价值续一下，顺便看有没有欠费"
 *   router 输出 domains = {renewal, servicing, finance}
 *   装配结果 = 这三个域工具的并集  → agent 可以跨着查
 * </pre>
 *
 * <p>工具数量爆炸时的扩展点（先别急着做）：
 * <ol>
 *   <li>工具描述写精炼，并集可控就先不做优化；</li>
 *   <li>工具 RAG：向量检索 top-K 相关工具；</li>
 *   <li>渐进加载：先挂主域，需要时再激活其他域。</li>
 * </ol>
 */
public final class DomainToolAssembler {

    private DomainToolAssembler() {
    }

    public static List<ToolSpec> assemble(Collection<DomainPlugin> domains) {
        List<ToolSpec> tools = new ArrayList<>();
        for (DomainPlugin domain : domains) {
            tools.addAll(domain.tools());
        }
        return tools;
    }
}
