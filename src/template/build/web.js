#!/usr/bin/env node

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');
const { promisify } = require('util');

// 将execSync转换为Promise
const exec = promisify(require('child_process').exec);

// 获取包管理器
function getPackageManager() {
    if (fs.existsSync('pnpm-lock.yaml')) {
        console.log('使用 pnpm 安装依赖');
        return 'pnpm';
    } else if (fs.existsSync('yarn.lock')) {
        console.log('使用 yarn 安装依赖');
        return 'yarn';
    } else {
        console.log('使用 npm 安装依赖');
        return 'npm';
    }
}

// 显示版本信息
function showVersions() {
    console.log(`node -v: ${execSync('node -v').toString().trim()}`);
    console.log(`npm -v: ${execSync('npm -v').toString().trim()}`);
    console.log(`yarn -v: ${execSync('yarn -v').toString().trim()}`);
    console.log(`pnpm -v: ${execSync('pnpm -v').toString().trim()}`);
}

// 安装依赖
async function installDependencies(pm) {
    try {
        await exec(`${pm} install`);
        console.log('依赖安装成功');
    } catch (error) {
        console.error('依赖安装失败:', error);
        process.exit(1);
    }
}

// 构建项目
async function buildProject(pm) {
    try {
        await exec(`${pm} build`);
        console.log('打包成功');
    } catch (error) {
        console.error('构建失败:', error);
        process.exit(1);
    }
}

// 部署构建产物
async function deployBuild(projectName) {
    const targetDir = `/usr/share/nginx/html/${projectName}`;
    
    try {
        // 删除已有的压缩包，重新打包
        if (fs.existsSync('dist.tar')) {
            fs.unlinkSync('dist.tar');
        }
        execSync('tar -zcvf dist.tar ./dist');
        console.log('压缩包创建成功');

        // 创建目标目录
        if (!fs.existsSync(targetDir)) {
            fs.mkdirSync(targetDir, { recursive: true });
            console.log(`📁 目录 ${targetDir} 已创建`);
        }

        // 复制到目标目录并解压
        fs.copyFileSync('dist.tar', path.join(targetDir, 'dist.tar'));
        process.chdir(targetDir);
        execSync('tar -zxvf ./dist.tar');
        console.log(`🎉 项目已部署到 ${targetDir}`);
    } catch (error) {
        console.error('部署失败:', error);
        process.exit(1);
    }
}

// 主函数
async function main() {
    try {
        // 从环境变量中读取项目名
        const projectName = process.env.JOB_NAME;
        if (!projectName) {
            throw new Error('未设置JOB_NAME环境变量');
        }

        // 显示版本信息
        showVersions();

        // 获取包管理器
        const pm = getPackageManager();

        // 安装依赖
        await installDependencies(pm);

        // 构建项目
        await buildProject(pm);

        // 部署构建产物
        await deployBuild(projectName);
    } catch (error) {
        console.error('构建过程出错:', error);
        process.exit(1);
    }
}

// 运行主函数
main(); 