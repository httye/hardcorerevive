# HardcoreRevive - 硬核复活系统

[![Build Status](https://github.com/httye/hardcorerevive/workflows/Maven%20Build/badge.svg)](https://github.com/httye/hardcorerevive/actions)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.20+-brightgreen.svg)](https://www.spigotmc.org/)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)

一个完整的 Minecraft Spigot/Paper 插件，实现硬核模式的死亡惩罚与复活机制。

## 功能特性

### 🧱 小黑屋系统
- 死亡后自动传送至小黑屋
- 冒险模式限制
- 完整的交互限制（破坏、放置、攻击、聊天等）
- 越狱检测与自动传送回
- 可选基岩+铁栅栏牢笼生成
- 多世界独立小黑屋支持

### 💎 复活码获取
1. **定时在线发放** - 可配置间隔和是否仅限存活玩家
2. **邀请新玩家奖励** - 新玩家游玩指定时间后奖励邀请者

### 🔐 复活码管理
- 8位随机唯一码（大小写不敏感）
- UUID绑定，仅限本人使用
- 可转赠给在线玩家
- 可设置有效期
- 完整的数据持久化

### ⚔️ 复活限制与惩罚
- 终身复活次数上限（可配置）
- 递增消耗机制（第N次复活消耗N个码）
- 复活冷却时间
- 经验等级惩罚

### 🤝 社交互助
- 信任列表系统
- 求救信号（SOS）- 全服音效+标题公告
- 悬赏系统 - 创建任务奖励复活码

### 🎨 氛围与反馈
- 死亡/复活全服公告
- Title大字特效
- 死亡点生成墓碑（玩家头颅+告示牌）
- 右侧计分板实时显示

### 🛠️ 管理工具
- GUI管理面板
- 批量操作命令
- 配置热重载
- 强制复活功能

## 命令列表

### 玩家命令
```
/revive <复活码> [玩家名]          - 复活自己或信任的死亡玩家
/revive send<玩家名> <复活码>     - 转赠复活码
/revive list                        - 查看自己的复活码
/sos                                - 发出求救信号
/trust add <玩家名>                 - 添加信任玩家
/trust remove <玩家名>              - 移除信任玩家
/trust list                         - 查看信任列表
/bounty create <描述>               - 创建悬赏
/bounty claim <悬赏ID>              - 领取悬赏
/bounty list                        - 查看悬赏列表
```

### 管理员命令
```
/setjail                - 设置小黑屋坐标
/reviveadmin generate <玩家> [数量] - 生成复活码
/reviveadmin list [玩家]            - 查看复活码信息
/reviveadmin gui                    - 打开管理面板
/reviveadmin giveall <数量>         - 给所有在线玩家发码
/reviveadmin reviveall              - 复活所有死亡玩家
/reviveadmin purgecodes             - 清理已用复活码
/reviveadmin forceRevive <玩家名>   - 强制复活（无视限制）
/hcreload                           - 重载配置文件
```

## 权限节点
```
hardcorerevive.revive    - 使用复活码 (默认: true)
hardcorerevive.sos       - 发送求救信号 (默认: true)
hardcorerevive.trust     - 管理信任列表 (默认: true)
hardcorerevive.bounty    - 使用悬赏系统 (默认: true)
hardcorerevive.admin     - 管理员权限 (默认: op)
```

## 配置文件

配置文件位于 `plugins/HardcoreRevive/config.yml`

主要配置项：
- `jail` - 小黑屋设置
- `revive_code` - 复活码设置
- `revive_limits` - 复活限制
- `invitation` - 邀请奖励
- `social` - 社交系统
- `tombstone` - 墓碑设置
- `announcements` - 公告设置
- `scoreboard` - 计分板设置
- `database` - 数据库设置

## 数据库支持

支持两种数据库类型：
1. **SQLite** (默认) - 无需额外配置
2. **MySQL** - 需要配置连接信息

## 编译与安装

### 前置要求
- Java 17+
- Maven 3.6+

### 编译步骤
```bash
cd /sdcard/project/HardcoreRevive
mvn clean package
```

编译完成后，JAR文件位于 `target/HardcoreRevive-1.0.0.jar`

### 安装
1. 将编译好的JAR文件放入服务器的 `plugins` 目录
2. 重启服务器或使用插件管理器加载
3. 编辑 `plugins/HardcoreRevive/config.yml` 进行配置
4. 使用 `/hcreload` 重载配置

## 项目结构

```
src/main/java/com/hardcorevive/
├── HardcoreRevivePlugin.java          # 主插件类
├── commands/                           # 命令处理器
│   ├── ReviveCommand.java
│   ├── SosCommand.java
│   ├── TrustCommand.java
│   ├── SetJailCommand.java
│   ├── BountyCommand.java
│   ├── ReviveAdminCommand.java
│   └── ReloadCommand.java
├── data/                               # 数据库管理
│   ├── DatabaseManager.java
│   └── DatabaseExtensions.java
├── listeners/                          # 事件监听器
│   ├── PlayerDeathListener.java
│   ├── PlayerJoinListener.java
│   ├── PlayerQuitListener.java
│   ├── JailProtectionListener.java
│   └── TombstoneProtectionListener.java
├── managers/                           # 功能管理器
│   ├── PlayerDataManager.java
│   ├── ReviveCodeManager.java
│   ├── JailManager.java
│   ├── TrustManager.java
│   ├── BountyManager.java
│   ├── TombstoneManager.java
│   ├── ScoreboardManager.java
│   └── InvitationManager.java
└── models/                             # 数据模型
    ├── PlayerData.java
    ├── ReviveCode.java
    └── Bounty.java
```

## 技术特性

- 完整的数据持久化
- 多线程安全
- 内存缓存优化
- 事件驱动架构
- 模块化设计
- 支持热重载

## 兼容性

- Spigot 1.20+
- Paper 1.20+
- Java 17+

## 许可证

MIT License

## 作者

httye

## 反馈与支持

如有问题或建议，请联系作者或提交Issue。
