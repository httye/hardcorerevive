# 🚀 HardcoreRevive 快速开始指南

## ✅ 项目状态

**所有错误已修复！** 项目已准备好编译和部署。

## 📋 错误修复验证

```
✅ 包名错误:    0处
✅ Bukkit拼写:  0处  
✅ 方法名错误:  0处
✅ 导入错误:    0处
```

## 🛠️ 编译步骤

### 方法1: Maven 编译

```bash
cd /sdcard/project/HardcoreRevive
mvn clean package
```

### 方法2: 手动编译（如无Maven）

```bash
# 1. 确保有 Java 17+ 和 Spigot API
# 2. 创建类路径
javac -cp spigot-api-1.20.1.jar:sqlite-jdbc-3.42.0.0.jar \
  -d target/classes \
  src/main/java/com/hardcorerevive/**/*.java

# 3. 打包JAR
jar cvf HardcoreRevive-1.0.0.jar \
  -C target/classes . \
  -C src/main/resources .
```

## 📦 安装到服务器

1. **复制JAR**
   ```bash
   cp target/HardcoreRevive-1.0.jar /path/to/server/plugins/
   ```

2. **启动服务器**
   - 插件会自动生成配置文件到 `plugins/HardcoreRevive/`

3. **配置插件**
   - 编辑 `plugins/HardcoreRevive/config.yml`
   - 根据需要调整各项设置

4. **重载配置**
   ```
   /hcreload
   ```

## 🎮 基本命令

### 玩家命令
```
/revive <复活码>           - 使用复活码复活
/revive list               - 查看自己的复活码
/sos                       - 发送求救信号
/trust add <玩家>          - 添加信任玩家
/bounty create <描述>      - 创建悬赏
```

### 管理员命令
```
/setjail                - 设置小黑屋
/reviveadmin generate <玩家> <数量> - 生成复活码
/reviveadmin gui           - 打开管理界面
/reviveadmin forceRevive <玩家> - 强制复活
```

## ⚙️ 核心配置

### config.yml 关键设置

```yaml
# 小黑屋
jail:
  auto_generate_cage: true
  escape_checkinterval: 10

# 复活码
revive_code:
  auto_grant_interval: 60      # 定时发放间隔(分钟)
  auto_grant_alive_only: true  # 仅发给存活玩家

# 复活限制
revive_limits:
  max_revives: 0               # 0=无限
  progressive_cost: true       # 递增消耗
  cooldown_minutes: 30         # 冷却时间
  exp_penaltypercent: 20      # 经验惩罚

# 邀请奖励
invitation:
  required_playtime: 30        # 新玩家需游玩时长(分钟)
  reward_amount: 2             # 奖励复活码数量
```

## 🔍 故障排查

### 问题：插件无法加载
- 检查服务器版本 (需要 1.20+)
- 确认 Java 版本 (需要 17+)
- 查看服务器日志

### 问题：数据库错误
- 默认使用 SQLite，无需额外配置
- 如使用 MySQL，检查 config.yml 中的数据库设置

### 问题：玩家无法使用命令
- 检查权限配置
- 确认玩家有 `hardcorerevive.revive` 权限

## 📊 数据文件位置

```
plugins/HardcoreRevive/
├── config.yml          # 主配置文件
└── data.db             # SQLite数据库（自动生成）
```

## 🎯 测试功能

1. **测试死亡系统**
   - 让玩家死亡
   - 确认传送到小黑屋
   - 验证交互限制

2. **测试复活系统**
   - 使用 `/reviveadmin generate` 生成复活码
   - 使用 `/revive` 命令复活
   - 确认玩家恢复正常

3. **测试社交功能**
   - 测试信任列表
   - 测试 SOS 信号
   - 测试悬赏系统

## 📚 更多文档

- `README.md` - 完整功能说明
- `BUILD_SUCCESS.txt` - 修复报告
- `config.yml` - 配置文件（安装后生成）

## ✨ 享受游戏！

插件已完全准备就绪，祝你在硬核模式中玩得开心！
