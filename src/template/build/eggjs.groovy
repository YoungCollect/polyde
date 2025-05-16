pipeline {
    agent any
    
    parameters {
        // Node.js版本选择
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
        // 标签参数
        // gitParameter(
        //     name: 'TAG_NAME',
        //     type: 'PT_TAG',
        //     description: '选择标签',
        //     defaultValue: 'RELEASE.1.0.0',
        //     branchFilter: 'origin/(.*)'
        // )
    }
    
    environment {
        // 定义环境变量
        NGINX_HTML_DIR = '/usr/share/nginx/html'
        // Git仓库配置
        GIT_REPO_URL = ''
        // 凭证ID配置
        GIT_CREDENTIALS_ID = ''
        // 根据构建类型设置仓库引用
        // REPO_REF = "${params.BUILD_TYPE == 'branch' ? params.BRANCH_NAME : params.TAG_NAME}"
        REPO_REF = "${params.BRANCH_NAME}"
    }
    
    stages {
        stage('环境准备') {
            steps {
                nvm('version': "${NODE_VERSION}") {
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
        
        // stage('依赖安装') {
        //     steps {
        //         nvm('version': "${NODE_VERSION}") {
        //             script {
        //                 // 检测包管理器
        //                 def pm = 'npm'
        //                 if (fileExists('pnpm-lock.yaml')) {
        //                     echo '使用 pnpm 安装依赖'
        //                     pm = 'pnpm'
        //                 } else if (fileExists('yarn.lock')) {
        //                     echo '使用 yarn 安装依赖'
        //                     pm = 'yarn'
        //                 } else {
        //                     echo '使用 npm 安装依赖'
        //                 }
                        
        //                 // 安装依赖
        //                 sh "${pm} install"
        //             }
        //         }
        //     }
        // }
        
        // stage('项目构建') {
        //     steps {
        //         nvm('version': "${NODE_VERSION}") {
        //             script {
        //                 // 检测包管理器
        //                 def pm = 'npm'
        //                 if (fileExists('pnpm-lock.yaml')) {
        //                     pm = 'pnpm'
        //                 } else if (fileExists('yarn.lock')) {
        //                     pm = 'yarn'
        //                 }
                        
        //                 // 构建项目
        //                 sh "${pm} build"
        //             }
        //         }
        //     }
        // }
        
        stage('部署') {
            steps {
                script {
                    // 创建目标目录
                    def targetDir = "${env.NGINX_HTML_DIR}/${env.JOB_NAME}"
                    sh """
                        # 删除已有的压缩包，重新打包
                        rm -rf dist.tar
                        tar --exclude='.git' -zcvf dist.tar .
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
        stage('docker-compose') {
            steps {
                script {
                    def dockerComposeBuilder = [$class: 'DockerComposeBuilder', dockerComposeFile: 'docker-compose.prod.yml', useCustomDockerComposeFile: true]
                    step(dockerComposeBuilder)
                }
            }
        }

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
            // 清理工作空间
            cleanWs()
        }
    }
}
