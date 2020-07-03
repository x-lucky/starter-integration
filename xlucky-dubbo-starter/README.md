## dubbo-start

使用：  
导入依赖  

```
<dependency>
	<groupId>cn.xlucky</groupId>
	<artifactId>xlucky-dubbo-starter</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```
nacos地址
http://localhost:8848/nacos/index.html#/


yml配置
```
dubbo:
  application:
    logger: slf4j
  protocol:
    name: dubbo
    port: -1
  registry:
    address: nacos://${nacos.server-address}:${nacos.port}/?username=${nacos.username}&password=${nacos.password}
  reference:
    check: false
  consumer:
    check: false
    retries: 0
    validation: true
    timeout: 600000
  provider:
    timeout: 600000
#服务者需要提供扫描路径
  scan:
    base-packages: cn.xlucky.framework.dubbo.test.provider

nacos:
  password: nacos
  port: 8848
  server-address: 127.0.0.1
  username: nacos
```





