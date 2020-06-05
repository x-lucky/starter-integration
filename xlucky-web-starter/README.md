## web-starter 日志功能，全局异常拦截，swagger

使用：  
导入依赖  

```
<dependency>
	<groupId>cn.xlucky</groupId>
	<artifactId>xlucky-web-starter</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```
yml配置
```
spring:
  application:
    name: xlucky-test
  profiles:
    active: ${env}



#异常处理
xlucky:
  exception:
    #自定义异常，小程序页面提示
    '10005': 非法的输入数据！
    '1001': 登陆失效，请重新登陆！
    '5000': 系统异常！
    '5001': 网络开小差！
    '5002': 调用服务{0}异常！
    '5003': 服务地址错误！
    '5004': 缺少必要的请求参数！
    '5005': HTTP Headers MediaType 错误！
    '5006': 参数绑定异常，请检查表单填写的数据！
    '5007': JSON转化异常！

swagger:
  title: 测试local
  description: 测试local
  version: 1.0
  basePackage: cn.xlucky.framework.test.controller
```

启动类需要添加扫描路径
@SpringBootApplication(scanBasePackages = "cn.xlucky.framework")





