package com.company.agent.core.spi;

import java.util.List;

/**
 * 域插件契约 —— 一个业务域（财务 / 续期 / 保全 / 消费者保护）接入 agent 平台的唯一入口。
 *
 * <p>设计要点：
 * <ul>
 *   <li>这是「契约」，放在平台内核里；各域去实现它。依赖方向只能是 域 → 内核。</li>
 *   <li>一个域至少要交出：意图表、工具、系统提示。权限/兜底有默认实现。</li>
 *   <li>加一个新域 = 新建一个实现类 + 放进资源，<b>内核一行都不用改</b>。</li>
 * </ul>
 *
 * <p>接入 Spring 时，实现类标 {@code @Component}，
 * 平台侧用一个 {@code List<DomainPlugin>} 就能拿到全部域（见 DomainRegistry 注释）。
 */
public interface DomainPlugin {

    /** 域的稳定标识，用于路由输出，例如 "finance" / "renewal"。 */
    String name();

    /** 域的中文名，用于日志/展示，例如 "财务"。 */
    String displayName();

    /** 意图表：这个域负责回答哪些意图。路由和评测都靠它。 */
    List<IntentSpec> intents();

    /** 工具清单：只放「业务动词」工具，不要暴露 CRUD / 表结构。 */
    List<ToolSpec> tools();

    /** 该域的系统提示：只写「角色 + 边界」，不要写死流程。 */
    String systemPrompt();

    /** 权限策略：同一个域里，不同工具可能要求不同权限。默认全放行。 */
    default PermissionPolicy permissions() {
        return PermissionPolicy.allowAll();
    }

    /** 兜底策略：答不了时转给谁、说什么。 */
    default FallbackPolicy fallback() {
        return FallbackPolicy.toHuman("客服");
    }
}
