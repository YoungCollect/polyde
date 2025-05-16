# Polyde

[nginx](./src/nginx/README.md) | [jenkins](./src/jenkins/README.md)

## 1.启动

安装 `docker` 之后：

```bash
docker-compose -f docker-compose.yml up
```

启动成功后，

`nginx` 监听 `80` 端口，访问 [http://localhost](http://localhost)。

`jenkins` 监听 `8080` 端口，访问 [http://localhost:8080](http://localhost:8080)。

## 2.部署

当部署新项目时，需要两步：

1. 在 `Jenkins UI` 中创建新项目，并且配置相关 `Shell` 或 `Pipeline`；
2. 在 `Nginx` 中新增代理端口。