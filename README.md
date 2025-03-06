# MC Payment 加密资产管理系统

## 需求背景
为了适应摩⽯集团业务的发展，⾯向Web3加密货币市场客户的出⼊⾦需求，提供对应的系统
解决⽅案。通过友好、⾼效、安全的解决⽅案，实现系统化的⾃动出⼊⾦能⼒，在保障资⾦安
全的前提下提升⽤户体验

## git branch
- dev 开发主分支
- test 测试主分支
- main 生产分支

## swagger访问地址
### mc-payment-core-service: 
- 本地:http://localhost:8000/api/swagger-ui/index.html
- 开发(走网关):http://192.168.2.155:9000/mc-payment/swagger-ui/index.html
- 测试(走网关):http://test.mc-payment.com/mc-payment/swagger-ui/index.html

### mc-payment-third-party-service:
本地:http://localhost:8100/api/swagger-ui/index.html
开发(走网关):http://192.168.2.155:9000/third-party/api/swagger-ui/index.html
测试(走网关):http://test.mc-payment.com/third-party/api/swagger-ui/index.html

### 网关配置
- https://nacos-test.mc-payment.com/nacos/#/configeditor?serverId=center&dataId=mc-payment-gateway-test.properties&group=DEFAULT_GROUP&namespace=test-mcgrp-namespace&edasAppName=&edasAppId=&searchDataId=&searchGroup=&pageSize=10&pageNo=1

### Nacos (服务发现&配置中心)
开发&测试:https://nacos-test.mc-payment.com/nacos/#/login nacos nacos

### XXL-JOB
测试: http://47.243.236.245:7000/xxl-job-admin  admin 123456

### 平台官网
https://www.magiccompasspay.com/
### 平台文档
https://docs.magiccompasspay.com/

## Project status
Under development

## 相关参考
MC Payment产品设计：https://www.notion.so/801d647014ba4195bb967d368018f2c7
MC Payment产品原型：https://modao.cc/proto/MSleUtMSsblr9fGzkhjZEV/sharing?view_mode=device&screen=rbpU9JjKPPTSQtInE&canvasId=rcU9JjKPU9RTMu6kgEwjIg
fireblocks官网：https://www.fireblocks.com/
fireblocks接口：https://ncw-developers.fireblocks.com/docs/getting-started
blockatm官网：https://blockatm.net/
blockatm接口：https://blockatm.gitbook.io/blockatm
Nacos:https://nacos.io/zh-cn/docs/what-is-nacos.html

## 项目打包
core-service: mvn clean package -Dmaven.test.skip=true   
注意:依赖了前端项目,需要安装前端依赖:nodejs 20.11.1 , pnpm 8.15.5
third-party-service: mvn clean package -Dmaven.test.skip=true
gateway: mvn clean package -Dmaven.test.skip=true