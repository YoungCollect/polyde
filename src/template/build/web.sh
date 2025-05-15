#!/bin/bash
set -e

# 显示版本信息
echo "node version: $(node -v)"
echo "npm version: $(npm -v)"
echo "yarn version: $(yarn -v)"
echo "pnpm version: $(pnpm -v)"

# 检测包管理器并设置命令
if [ -f "pnpm-lock.yaml" ]; then
    echo "使用 pnpm 安装依赖"
    pm="pnpm"
elif [ -f "yarn.lock" ]; then
    echo "使用 yarn 安装依赖"
    pm="yarn"
else
    echo "使用 npm 安装依赖"
    pm="npm"
fi

# 安装依赖
$pm install
echo "依赖安装成功"

# 构建项目
$pm build
echo "打包成功"

# 删除已有的压缩包，重新打包
rm -rf dist.tar
tar -zcvf dist.tar ./dist  
echo "压缩包创建成功"

# 创建目标目录
TARGET_DIR="/usr/share/nginx/html/${JOB_NAME}"
if [ ! -d "$TARGET_DIR" ]; then
    mkdir -p "$TARGET_DIR"
    echo "📁 目录 $TARGET_DIR 已创建"
fi

# 复制到目标目录并解压
cp dist.tar "$TARGET_DIR/"
cd "$TARGET_DIR"
tar -zxvf ./dist.tar
echo "🎉 项目已部署到 $TARGET_DIR"
