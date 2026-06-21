#!/bin/bash

echo "🚀 HardcoreRevive - GitHub推送脚本"
echo "================================"
echo ""

# 检查是否提供了GitHub用户名
if [ -z "$1" ]; then
    echo "用法: ./push_to_github.sh YOUR_GITHUB_USERNAME"
    echo ""
    echo "示例: ./push_to_github.sh john-doe"
    exit 1
fi

USERNAME=$1
REPO_URL="https://github.com/$USERNAME/HardcoreRevive.git"

echo "仓库URL: $REPO_URL"
echo ""

# 检查是否已有remote
if git remote | grep -q origin; then
    echo "⚠️  远程仓库已存在，移除旧的..."
    git remote remove origin
fi

# 添加远程仓库
echo "📦 添加远程仓库..."
git remote add origin $REPO_URL

# 推送
echo "🔄 推送到GitHub..."
echo "   (首次推送可能需要输入GitHub用户名和Token)"
echo ""

git push -u origin master --force

echo ""
echo "✅ 推送完成！"
echo ""
echo "📋 下一步:"
echo "1. 访问: https://github.com/$USERNAME/HardcoreRevive"
echo "2. 点击 Actions 查看自动编译状态"
echo "3. 编译完成后在 Artifacts 下载JAR文件"
echo ""
echo "📖 详细说明请查看: GITHUB_PUSH.md"
