package com.company.agent.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 从 classpath 读资源的小工具。
 *
 * <p>域的提示词、意图表、槽位定义都放在各域自己的资源目录下，
 * 这样<b>业务专家改这些文件不用碰代码</b>——这是给非开发同事留的口子。
 */
public final class Resources {

    private Resources() {
    }

    public static String load(String path, String fallback) {
        try (InputStream in = Resources.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                return fallback;
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return fallback;
        }
    }
}
