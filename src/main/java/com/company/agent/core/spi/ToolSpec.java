package com.company.agent.core.spi;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 工具契约。
 *
 * <p>命名规范：<b>工具 = 一个语义完整的「业务动作」</b>，不是数据库的增删改查。
 * <pre>
 *   ✅ finance.queryArrears     查询欠费明细
 *   ✅ servicing.tryCashValue   试算现金价值
 *   ✅ servicing.surrender      办理犹豫期退保（业务规则封在工具内部）
 *   ❌ updatePolicyField        改字段（模型会拼错业务逻辑，规则也拦不住）
 *   ❌ execSql                  裸 SQL
 * </pre>
 *
 * <p>经验：<b>读/查询可以粗，写/动作必须细</b>。因为写操作出事就是事故，要用「业务动作」把风险锁住。
 */
public interface ToolSpec {

    /** 全局唯一的工具名，建议 {@code 域.动作} 形式。 */
    String name();

    /** 给模型看的描述：说清「什么时候用、做什么」。这句写得好，模型就选得准。 */
    String description();

    /** 参数定义。 */
    List<ParamSpec> parameters();

    /** 执行。入参是已解析好的参数表，返回任意可序列化对象。 */
    Object invoke(Map<String, Object> args);

    /** 参数定义。 */
    record ParamSpec(String name, String type, String description, boolean required) {
        public static ParamSpec required(String name, String type, String description) {
            return new ParamSpec(name, type, description, true);
        }

        public static ParamSpec optional(String name, String type, String description) {
            return new ParamSpec(name, type, description, false);
        }
    }

    /** 便捷工厂。 */
    static ToolSpec of(String name,
                       String description,
                       List<ParamSpec> parameters,
                       Function<Map<String, Object>, Object> handler) {
        return new ToolSpec() {
            @Override public String name() { return name; }
            @Override public String description() { return description; }
            @Override public List<ParamSpec> parameters() { return parameters; }
            @Override public Object invoke(Map<String, Object> args) { return handler.apply(args); }
        };
    }
}
