# Polaris Microservice Framework Project
Msf由前公司项目剥离出来，从入职时 SSH + Hessian 到 Dubbo SrpingBoot，从最开始的单一服务应用到分布式服务，耗费了很多的时光；为了保证服务的稳定安全监控等要求，也加入了各种优秀的组件。开源，一方面方便继续学习，跟上技术前沿的潮流；另一方面也算是提供了一种技术解决方案。

## Dubbo服务
## 注册中心
注册中前期采用采用ZooKeeper，后期考虑接入Nacos
多注册中心：
低版本在启动类上加入@EnableDubbo(multipleConfig = true)就可以开启多注册中心
例如支付的注册中心、大数据服务的注册中心等等，而对于这些注册中心我们基本上都只需要订阅服务，不需要发布，具体可以配置。

## 消息推送

## 安全网关

## logback日志
步骤：
1.导入logback相关jar包
2.resources下加入logback-spring.xml，可以从application-**.properties读取相关值

## 代码生成


## 接口文档
之前使用Doclever，自己编写接口文档，不会产生代码侵入，可在线生成对接文档等，适用于前后端分离、多人联合开发、测试上下文测试，提高对接及测试的效率。但是需要搭建Doclever环境，此处为了方便使用Swagger2。
步骤：
1.导入swagger2相关的类；
2.添加配置类Swagger2Config；
3.在对外接口上添加相关注解，如UserController。
可参考https://www.jianshu.com/p/c79f6a14f6c9

## 未来发展
### 结合微服务架构及中台思想
1. 通用服务下沉
2. 引入前端

