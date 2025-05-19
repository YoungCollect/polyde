pipeline {
    agent any
    
    parameters {
        string(
            name: 'NODE_VERSION',
            defaultValue: '22.15.0',
            description: '输入 Node.js 版本，例如：16.13.0、18.16.0、22.18.0'
        )
        // 构建类型选择
        choice(name: 'BUILD_TYPE', choices: ['branch', 'tag'], description: '选择构建类型')
        // 分支参数
        gitParameter(
            name: 'BRANCH_NAME',
            type: 'PT_BRANCH',
            description: '选择分支',
            defaultValue: 'master',
            branchFilter: 'origin/(.*)'
        )
    }
    
    environment {
        // 定义环境变量
        NGINX_HTML_DIR = '/usr/share/nginx/html'
        // Git仓库配置
        GIT_REPO_URL = ''
        // 凭证ID配置
        GIT_CREDENTIALS_ID = ''
        // 根据构建类型设置仓库引用
        REPO_REF = "${params.BRANCH_NAME}"
    }
    
    stages {
        stage('环境准备') {
            steps {
                nvm('version': "${NODE_VERSION}", 'nvmInstallURL': 'https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.3/install.sh') {
                    // 显示版本信息
                    sh '''
                        echo "node version: \$(node -v), npm version: \$(npm -v), yarn version: \$(yarn -v), pnpm version: \$(pnpm -v)"
                    '''
                }
            }
        }
        
        stage('代码检出') {
            steps {
                script {
                    // 清理工作空间
                    cleanWs()
                    
                    // 检出代码
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: "${REPO_REF}"]],
                        userRemoteConfigs: [[
                            url: "${env.GIT_REPO_URL}",
                            credentialsId: "${env.GIT_CREDENTIALS_ID}"
                        ]],
                        extensions: [
                            [$class: 'CleanBeforeCheckout'],
                            [$class: 'CloneOption', depth: 1, noTags: false, shallow: true]
                        ]
                    ])
                    
                    // 显示当前检出信息
                    echo "当前检出: ${params.BUILD_TYPE} - ${REPO_REF}"
                }
            }
        }
        
        stage('部署') {
            steps {
                script {
                    // 创建目标目录
                    def targetDir = "${env.NGINX_HTML_DIR}/${env.JOB_NAME}"
                    sh """
                        # 删除已有的压缩包，重新打包
                        rm -rf dist.tar
                        tar -zcvf dist.tar -T tar.txt
                        echo '压缩包创建成功'
                        
                        # 创建目标目录
                        mkdir -p ${targetDir}
                        echo '📁 目录 ${targetDir} 已创建'
                        
                        # 复制到目标目录并解压
                        cp dist.tar ${targetDir}/
                        cd ${targetDir}
                        tar -zxvf ./dist.tar
                        echo '🎉 项目已部署到 ${targetDir}'
                    """
                }
            }
        }
        stage('Check and Run Docker Compose') {
            steps {
                script {
                    // 检查是否已有容器在运行
                    def runningContainers = sh(script: "docker compose -f docker-compose.prod.yml ps -q", returnStdout: true).trim()
                    
                    if (runningContainers) {
                        echo 'Containers are running. Checking for updates...'
                        
                        // 更新最新镜像
                        sh 'docker compose -f docker-compose.prod.yml pull'
                        
                        // 重新创建并启动有更新的服务
                        sh 'docker compose -f docker-compose.prod.yml up -d --force-recreate --remove-orphans'
                        
                        echo 'Services updated and restarted.'
                    } else {
                        echo 'No running containers. Starting new ones...'
                        sh 'docker compose -f docker-compose.prod.yml up -d'
                        echo 'Services started.'
                    }
                }
            }
        }
        // stage('docker-compose') {
        //     steps {
        //         step([
        //             $class: 'DockerComposeBuilder',
        //             dockerComposeFile: "${env.NGINX_HTML_DIR}/${env.JOB_NAME}/docker-compose.prod.yml",
        //             useCustomDockerComposeFile: true,
        //             option: [
        //                 $class: 'StartService',
        //                 scale: 1,
        //                 service: 'app'
        //             ]
        //         ])
        //     }
        // }
    }
    
    post {
        success {
            echo '构建成功！'
            // 可以添加成功通知，如邮件、钉钉等
        }
        failure {
            echo '构建失败！'
            // 可以添加失败通知
        }
        always {
            cleanWs()
        }
    }
}
