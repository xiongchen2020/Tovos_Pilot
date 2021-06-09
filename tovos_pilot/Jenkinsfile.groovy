@Library('shared-library') _

pipeline {
    agent any
    parameters { //定义构建参数
        choice choices: ['-'], description: '请选择部署方式', name: 'deploy-choice'
    }
    stages {
        stage('初始化') {
            steps {
                script{
                    //加载源码仓库根目录下的jenkins-project.json构建配置文件
                    runWrapper.loadJSON('/jenkins-project.json')
                    runWrapper.runSteps('初始化')
                }
            }
        }
        stage('单元测试') {
            steps {
                script{
                    //执行单元测试步骤
                    runWrapper.runSteps('单元测试')
                }
            }
        }
        stage('代码检查') {
            steps {
                script{
                    //执行代码检查步骤，比如SonarQube
                    runWrapper.runSteps('代码检查')
                }
            }
        }
        stage('编译构建') {
            steps {
                script{
                    //执行编译步骤
                    runWrapper.runSteps('编译构建')
                }
            }
        }
        stage('部署') {
            steps {
                script{
                    //根据选择的部署方式执行部署步骤
                    runWrapper.runStepForEnv('部署','deploy-choice')
                }
            }
        }
    }
}