# 使用官方推荐且更轻量安全的基础镜像（例如基于 Alpine 的 Nginx）
FROM nginx:1.28.0-alpine

# 拷贝配置文件
COPY conf.d/ /etc/nginx/conf.d/

# RUN mkdir -p /usr/share/nginx/html && \
# chmod -R 755 /usr/share/nginx/html

# 暴露端口
EXPOSE 80
