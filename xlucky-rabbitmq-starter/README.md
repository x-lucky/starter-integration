## rabbit配置

使用：  
导入依赖  

```
<dependency>
	<groupId>cn.xlucky</groupId>
	<artifactId>xlucky-rabbitmq-starter</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```
yml配置
```
rabbitmq:
    address: localhost:5672
    concurrency: 5
    password: test
    prefetchCount: 1
    username: uset
    virtualHost: /xlucky.dev
```






