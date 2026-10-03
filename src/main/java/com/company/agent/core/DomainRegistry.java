package com.company.agent.core;

import com.company.agent.core.spi.DomainPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 域注册表 —— 平台侧收集所有域的地方。这是「加一个域，内核不改」的关键。
 *
 * <p>纯 Java 版：手动 register。
 *
 * <p>接入 Spring 时，把这个类改成一个 {@code @Service}，构造器注入 {@code List<DomainPlugin>} 即可：
 * <pre>
 *   @Service
 *   public class DomainRegistry {
 *       private final Map&lt;String, DomainPlugin&gt; byName;
 *       public DomainRegistry(List&lt;DomainPlugin&gt; plugins) {   // Spring 自动注入全部实现
 *           this.byName = plugins.stream()
 *                   .collect(Collectors.toMap(DomainPlugin::name, Function.identity()));
 *       }
 *   }
 * </pre>
 * 这样各域只要是一个 {@code @Component}，就被自动收进来了。
 */
public class DomainRegistry {

    private final Map<String, DomainPlugin> byName = new LinkedHashMap<>();

    public DomainRegistry register(DomainPlugin plugin) {
        if (byName.containsKey(plugin.name())) {
            throw new IllegalStateException("域重复注册: " + plugin.name());
        }
        byName.put(plugin.name(), plugin);
        return this;
    }

    public Optional<DomainPlugin> get(String name) {
        return Optional.ofNullable(byName.get(name));
    }

    /** 全部已注册的域。 */
    public Collection<DomainPlugin> all() {
        return Collections.unmodifiableCollection(byName.values());
    }

    /** 按名字解析出一组域（路由输出的是「集合」，所以这里是集合入参）。 */
    public List<DomainPlugin> resolve(Collection<String> names) {
        List<DomainPlugin> resolved = new ArrayList<>();
        for (String name : names) {
            get(name).ifPresent(resolved::add);
        }
        return resolved;
    }
}
