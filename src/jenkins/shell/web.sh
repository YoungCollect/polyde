#!/bin/bash
set -e

PROJECT_NAME=${JOB_NAME}
TARGET_DIR="/usr/share/nginx/html/${PROJECT_NAME}"

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

# ✅ 1. 创建目标目录并提前修正权限
if [ ! -d "$TARGET_DIR" ]; then
    mkdir -p "$TARGET_DIR"
    echo "📁 目录 $TARGET_DIR 已创建"
fi

# ✅ 2. 提前修正目标目录权限，避免后续操作失败
chown -R 101:101 "$TARGET_DIR"
chmod -R 755 "$TARGET_DIR"
echo "✅ 目录权限已修正"

# ✅ 3. 复制到目标目录并解压
cp dist.tar "$TARGET_DIR/"
cd "$TARGET_DIR"
tar -zxvf ./dist.tar
echo "🎉 项目已部署到 $TARGET_DIR"

# ✅ 4. 最后再次确认权限，以防万一
chown -R 101:101 "$TARGET_DIR"
chmod -R 755 "$TARGET_DIR"
echo "✅ 部署完成，Nginx 可以正常读取"
