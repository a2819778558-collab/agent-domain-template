package com.company.agent.core.spi;

import java.util.List;

/**
 * 一条意图定义。
 *
 * <p>意图表是整个路由的「分类标准」。它比选什么模型重要得多——
 * 因为分类器再准，也得先有「有哪几类」这个定义。
 *
 * <p>建议：意图粒度对齐「执行单元」——一个意图大致对应一个可完成的动作或一组工具，
 * 不要细到每个字段，也不要粗到「问答」。
 */
public record IntentSpec(String id, String description, List<String> examples) {

    public static IntentSpec of(String id, String description, String... examples) {
        return new IntentSpec(id, description, List.of(examples));
    }
}
