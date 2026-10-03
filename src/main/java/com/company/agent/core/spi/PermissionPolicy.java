package com.company.agent.core.spi;

/**
 * 权限策略 —— 保险合规的命脉。
 *
 * <p>注意：<b>路由到某个域 ≠ 有权调用该域的工具</b>。
 * 财务数据敏感，路由之后必须再经过这一层鉴权，不能因为「意图识别判成了财务」就放行。
 */
@FunctionalInterface
public interface PermissionPolicy {

    /**
     * @param userId   当前用户
     * @param toolName 要调用的工具名
     * @return 是否允许
     */
    boolean canAccess(String userId, String toolName);

    static PermissionPolicy allowAll() {
        return (userId, toolName) -> true;
    }

    /** 只允许访问指定前缀的工具，例如 "finance." 开头。 */
    static PermissionPolicy allowPrefix(String... prefixes) {
        return (userId, toolName) -> {
            for (String p : prefixes) {
                if (toolName.startsWith(p)) return true;
            }
            return false;
        };
    }
}
