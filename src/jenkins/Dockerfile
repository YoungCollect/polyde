FROM docker.1ms.run/jenkins/jenkins:lts-jdk17

# 切换到root用户以安装软件包
USER root

# 安装curl和nodejs
RUN apt-get update && \
    apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs

# 这个官方脚本会安装docker ce和docker cli，jenkins镜像容器不支持dcoker ce.
# RUN curl -fsSL https://get.docker.com -o get-docker.sh && \
#     sh get-docker.sh --mirror Aliyun && \
#     rm get-docker.sh

# 这个是centos的docker cli安装脚本
# RUN yum install -y docker-ce-cli-23.0.1-1.el7

# 这个是debian的docker cli安装脚本
RUN mkdir -p /etc/apt/keyrings && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --yes --dearmor -o /etc/apt/keyrings/docker.gpg && \
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/debian bullseye stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null && \
    apt-get update && \
    apt-get install -y docker-ce-cli

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
