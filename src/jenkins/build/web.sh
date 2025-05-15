#!/bin/bash
set -e

# 从环境变量中读取项目名
PROJECT_NAME=${JOB_NAME}

# 显示版本信息
node -v 
npm -v 

# 安装依赖
npm install
echo "依赖安装成功"

# 构建项目
npm run build
echo "打包成功"

# 删除已有的压缩包，重新打包
rm -rf dist.tar
tar -zcvf dist.tar ./dist  
echo "压缩包创建成功"

# 创建目标目录
TARGET_DIR="/usr/share/nginx/html/${PROJECT_NAME}"
if [ ! -d "$TARGET_DIR" ]; then
    mkdir -p "$TARGET_DIR"
    echo "📁 目录 $TARGET_DIR 已创建"
fi

# 复制到目标目录并解压
cp dist.tar "$TARGET_DIR/"
cd "$TARGET_DIR"
tar -zxvf ./dist.tar
echo "🎉 项目已部署到 $TARGET_DIR"