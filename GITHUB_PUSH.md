# 🚀 GitHub 推送指南

## 准备工作

### 1. 在GitHub上创建仓库

1. 访问 https://github.com/new
2. 仓库名称: `HardcoreRevive`
3. 描述: `Minecraft硬核复活系统插件 - A hardcore revival system plugin for Minecraft`
4. 选择 **Public** 或 **Private**
5. **不要**初始化README、.gitignore或LICENSE（我们已经有了）
6. 点击 "Create repository"

### 2. 获取仓库URL

创建后，GitHub会显示类似这样的URL：
```
https://github.com/YOUR_USERNAME/HardcoreRevive.git
```

记下这个URL，将 `YOUR_USERNAME` 替换为你的GitHub用户名。

## 推送步骤

### 方法1: HTTPS推送（推荐）

```bash
cd /sdcard/project/HardcoreRevive

# 添加远程仓库
git remote add origin https://github.com/YOUR_USERNAME/HardcoreRevive.git

# 推送到GitHub
git push -u origin master
# 或者如果主分支是main:
git push -u origin main
```

首次推送时会要求输入GitHub用户名和密码（或Personal Access Token）。

### 方法2: SSH推送

如果已配置SSH密钥：

```bash
cd /sdcard/project/HardcoreRevive

# 添加远程仓库（SSH）
git remote add origin git@github.com:YOUR_USERNAME/HardcoreRevive.git

# 推送
git push -u origin master
```

### 如果推送失败

如果看到 "failed to push some refs" 错误：

```bash
# 强制推送（首次推送时使用）
git push -u origin master --force
```

## GitHub Actions 自动编译

推送成功后：

1. 访问你的仓库页面
2. 点击 "Actions" 标签
3. 你会看到自动触发的构建任务
4. 等待几分钟，编译完成后
5. 点击构建任务，在 "Artifacts" 部分下载编译好的JAR文件

## 自动编译说明

### 触发条件

GitHub Actions会在以下情况自动编译：
- 推送代码到 main/master 分支
- 创建Pull Request
- 手动触发（在Actions页面点击"Run workflow"）

### 编译产物

编译成功后，可以在以下位置获取JAR文件：
1. **Actions页面** → 选择构建 → **Artifacts** → 下载 `HardcoreRevive-Plugin`
2. **Releases页面** → 自动创建的版本 → 下载附件

### 查看编译状态

在README.md中的徽章会显示编译状态：
- 🟢 绿色 = 编译成功
- 🔴 红色 = 编译失败
- 🟡 黄色 = 正在编译

## 更新README中的用户名

推送后，记得更新README.md中的徽章链接：

```markdown
将 YOUR_USERNAME 替换为你的GitHub用户名
```

## 常见问题

### Q: 推送时要求输入密码？
A: GitHub已不再支持密码推送，需要使用Personal Access Token：
   1. GitHub → Settings → Developer settings → Personal access tokens
   2. Generate new token (classic)
   3. 选择 `repo` 权限
   4. 生成后复制token
   5. 推送时用token替代密码

### Q: 编译失败？
A: 检查Actions日志：
   1. 进入Actions页面
   2. 点击失败的构建
   3. 查看红色的步骤
   4. 展开查看详细错误信息

### Q: 如何手动触发编译？
A: 
   1. 进入仓库的Actions页面
   2. 选择 "Maven Build" 工作流
   3. 点击 "Run workflow"
   4. 选择分支并运行

## 验证推送成功

推送成功后，你应该能在GitHub上看到：
- ✅ 所有源代码文件
- ✅ README.md 正常显示
- ✅ Actions 标签显示构建状态
- ✅ 几分钟后出现编译好的JAR文件

## 下载编译好的插件

### 方法1: 从Actions下载
```
1. GitHub仓库 → Actions
2. 点击最新的成功构建
3. 向下滚动到 Artifacts
4. 点击下载 HardcoreRevive-Plugin.zip
5. 解压获得 .jar 文件
```

### 方法2: 从Releases下载
```
1. GitHub仓库 → Releases
2. 选择最新版本
3. 在 Assets 中下载 .jar 文件
```

---

## 🎉 完成！

推送成功后，你的插件就托管在GitHub上了，并且每次推送都会自动编译！

**仓库示例**: `https://github.com/YOUR_USERNAME/HardcoreRevive`
