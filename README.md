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

## 2.Jenkins UI

在第一步中启动 `jenkins` 后，访问 [http://localhost:8080](http://localhost:8080)，会提示输入管理员密码。

![](./assets/images/jenkins/1.password.png)

在 `docker-compose.yml` 中，`jenkins` 的管理员密码存放在 `/var/jenkins_home/secrets/initialAdminPassword` 中。

输入密码后，提示安装插件，选择 `Install suggested plugins`。

![](./assets/images/jenkins/2.install-plugin-choice.png)

安装进程如下：

![](./assets/images/jenkins/3.install-plugin-recommend.png)

安装完成后，会提示创建用户。当然也可以使用默认的 `admin` 用户（该用户的密码存储地，上文已经提过了）。

![](./assets/images/jenkins/4.create-user.png)

创建用户后，提示配置 `Jenkins` URL，默认是 `http://localhost:8080/`，可以直接点击 `Save and Finish`。

![](./assets/images/jenkins/5.jenkins-url.png)

最后，我们就可以登录 `Jenkins UI` 了。

![](./assets/images/jenkins/6.complete.png)

:::tip
**如果在安装插件的过程中，有安装失败的插件，那么我们可以在 `Jenkins UI` 中，手动搜索安装对应插件**。
:::

## 3.Jenkins Plugins



