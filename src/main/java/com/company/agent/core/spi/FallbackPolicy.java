package com.company.agent.core.spi;

/**
 * 兜底策略。
 *
 * <p>原则：<b>识别不了、做不了，就老实转人工，不要硬猜。</b>
 * 在保险场景里，一次错误路由的代价远大于一次「我不知道」。
 */
public record FallbackPolicy(String humanGroup, String message) {

    public static FallbackPolicy toHuman(String group) {
        return new FallbackPolicy(group, "这个问题我暂时无法处理，正在为您转接" + group + "。");
    }
}
