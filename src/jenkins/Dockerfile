FROM docker.1ms.run/jenkins/jenkins:lts-jdk17

# 切换到root用户以安装软件包
USER root

# 安装curl和nodejs
RUN apt-get update && \
    apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs

# 使用 Docker 官方安装脚本安装 docker 和 docker compose
# RUN curl -fsSL https://get.docker.com -o get-docker.sh && \
#     sh get-docker.sh --mirror Aliyun && \
#     rm get-docker.sh
    
# 安装yarn
RUN npm install -g yarn

# 安装pnpm
RUN npm install -g pnpm

# 设置环境变量
ENV PATH="/usr/local/bin:${PATH}"

# 切换回jenkins用户
USER jenkins

# 暴露端口
EXPOSE 8080 50000
