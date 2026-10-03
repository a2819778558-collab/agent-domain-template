# agent-domain-template

多域 Agent 的「**域插件契约**」参考骨架。

一句话：**把「域」从 agent 内核里解耦出来，让四个业务组（财务 / 续期 / 保全 / 消费者保护）能各自往里填，而内核一行都不用改。**

---

## 为什么需要这个

你现在的财务 agent 是「一个 loop + 一份写死的 prompt + 4 个工具」。要做四个域时会卡在两个地方：

1. **prompt 越写越死**——把业务流程写进提示词，模型只能走你画好的路，感觉束手束脚；
2. **没法复制到别的域**——领域知识和内核揉在一起，加一个域就得动内核。

本骨架给出的答案是分三层：

```
┌─────────────────────────────────────────────┐
│ 平台层（域无关，做一次，四域共用）             │
│ AgentLoop · Router · ToolRegistry · Memory    │
├─────────────────────────────────────────────┤
│ 路由层（意图 → 域集合）                        │
├─────────────────────────────────────────────┤
│ 域层（每域一份配置，可插拔）                    │
│ 财务 │ 续期 │ 保全 │ 消费者保护                 │
└─────────────────────────────────────────────┘
```

### 三条核心原则

| 原则 | 说明 |
|------|------|
| **域是管理单位，不是执行牢笼** | 路由输出的是「**涉及哪些域**」的集合；跨域问题就装配多域工具**并集** |
| **工具 = 业务动词，不是 CRUD** | `servicing.surrender`（办理退保）✅　`updatePolicyField` ❌ |
| **流程写在 skill/tool 里，不写在 prompt 里** | prompt 只写「角色 + 边界」，越写流程越束手束脚 |

### Tool / Skill / Router 的分工

| 层 | 干什么 | 例子 |
|----|--------|------|
| **Tool** | 一个原子业务动作 | `renewal.calcPremium` |
| **Skill** | 一串动作的已知流程（编排） | 「续期办理」= 查状态→算保费→确认→提交 |
| **Router** | 决定进哪个/哪几个域 | 涉及 `{renewal, servicing, finance}` |

> MCP 管「怎么把工具接进来」，Skill 管「怎么教模型做事」，**域包**管「归谁、装什么」。

---

## 目录结构

```
src/main/java/com/company/agent/
├── core/                          # 平台内核（域无关）
│   ├── DomainRegistry.java        # 收集所有域（Spring 里注入 List<DomainPlugin>）
│   ├── DomainToolAssembler.java   # 多域工具并集
│   ├── Resources.java
│   └── spi/                       # ★ 契约在这里
│       ├── DomainPlugin.java      # 域的唯一入口
│       ├── ToolSpec.java          # 工具（业务动词）
│       ├── IntentSpec.java        # 意图
│       ├── PermissionPolicy.java  # 权限
│       └── FallbackPolicy.java    # 兜底
├── router/                        # 路由层
│   ├── Router.java
│   ├── RouteResult.java           # 注意 domains 是 Set
│   └── KeywordRouter.java         # 演示用最简实现
├── domains/                       # 各域实现
│   ├── finance/                   # ★ 完整样板
│   ├── renewal/                   # 骨架模板
│   ├── servicing/                 # 骨架模板
│   └── protection/                # 骨架模板
└── app/DomainTemplateApp.java     # 演示入口

src/main/resources/
├── finance/{system.md, intents.yml, slots.yml}
└── renewal/{system.md, intents.yml}
```

**依赖方向只能是 `域 → 内核`**，内核不认识任何具体域。反了就废了。

---

## 跑起来

```bash
cd ~/project/agent-domain-template
mvn -q compile exec:java
```

你会看到第一条问题**同时命中 renewal / servicing / finance 三个域**，装配出三域工具的并集，并跨域取数——这正是「跨域问题不会被单域困住」的证据。

---

## 接入一个新域（三步）

1. 在 `domains/` 下新建包，写一个 `XxxDomain implements DomainPlugin`
2. 在 `resources/xxx/` 下放 `intents.yml`、`system.md`、`slots.yml`
3. 在 `DomainTemplateApp`（或 Spring 的自动收集）里注册

**内核一行都不用改。**

---

## 接进你现有的 `agent-framework-java-v2`

| 本骨架 | 你项目里对应 | 怎么接 |
|--------|--------------|--------|
| `DomainPlugin` | 新增 | 直接加，实现类标 `@Component` |
| `DomainRegistry` | 新增 | 改成 `@Service`，构造器注入 `List<DomainPlugin>` |
| `ToolSpec` | `tool/ToolRegistry` + LangChain4j `@Tool` | 用一个适配器把 `ToolSpec` 包到 `@Tool` 方法上 |
| `IntentSpec` / `intents.yml` | 新增 | 路由和评测的标准 |
| `Router` | **新增（当前缺的就是这层）** | 建议级联：规则 → 向量 → LLM 结构化分类 → 澄清 |
| `systemPrompt()` | `prompt/parts/SystemPromptPart` | 每个域一份提示词，替代现在写死的那份 |
| `skills/*.md` | `skill/SkillRegistry` | 保持不动，按域归属即可 |

**最小改动路径**：先只做两件事——
1. 把现有财务的 prompt + 4 个工具抽成 `FinanceDomain`；
2. 加一个 `Router`，输出「涉及哪几个域」。

做完这两步，你既验证了架构，也把 prompt 从「规则手册」里解放出来了。

---

## 下一步（建议顺序）

1. 拿本骨架对齐财务域，确认契约够用；
2. 和续期组开一次「意图表工作坊」，把 `renewal/intents.yml` 填真；
3. 路由器换成级联实现；
4. 建 golden set（几百条真实问法），量准「域准确率 / 跨域召回率 / 澄清率」；
5. 逐域接入，每接一个域更新一次意图表。

> 详见 `docs/域接入登记表.md`——给业务专家填的表。
