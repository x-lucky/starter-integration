## nacos依赖

使用：  
导入依赖  

```
<dependency>
	<groupId>cn.xlucky</groupId>
	<artifactId>xlucky-nacos-starter</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```
yml配置
```
spring:
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
#        命名空间id
        namespace: 7f899343-23c1-4205-826c-581a9f4cf88a
```





