FROM jenkins/jenkins:lts-jdk17

# 切换到root用户以安装软件包
USER root

# 安装curl和nodejs
RUN apt-get update && \
    apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs

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
