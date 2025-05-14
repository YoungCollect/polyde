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

## 2.配置Jenkins

在第一步中启动 `jenkins` 后，访问 [http://localhost:8080](http://localhost:8080)，会提示输入管理员密码。

![](./assets/images/jenkins/1.password.png)

在 `docker-compose.yml` 中，`jenkins` 的管理员密码存放在 `/var/jenkins_home/secrets/initialAdminPassword` 中。

输入密码后，提示安装插件，选择 `Install suggested plugins`。

![](./assets/images/jenkins/2.install-plugin-choice.png)

安装进程如下：

![](./assets/images/jenkins/3.install-plugin-recommend.png)

<!-- 在安装插件的过程中，可能会提示 `Unable to connect to the remote server`，这时需要手动安装插件。
在 `Manage Jenkins` -> `Manage Plugins` 中，选择 `Available` 标签页，搜索 `git` 和 `docker`，安装这两个插件。
在安装完成后，点击 `Manage Jenkins` -> `Configure System`，配置 `Git` 和 `Docker` 的路径。
在 `Manage Jenkins` -> `Manage Plugins` 中，选择 `Installed` 标签页，搜索 `git` 和 `docker`，查看插件是否安装成功。
在 `Manage Jenkins` -> `Manage Plugins` 中，选择 `Updates` 标签页，查看插件是否有更新。 -->


