## 实现热更新配置

使用：  
导入依赖  

```
<dependency>
	<groupId>cn.xlucky</groupId>
	<artifactId>xlucky-apollo-starter</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```
yml配置
```
app:
  id: app-ms

apollo:
  meta: http://apollo-xlucky.cn
  bootstrap:
    namespaces: application
    enabled: true
    #命名空间，不指定默认 application
    eagerLoad:
      enabled: true
```





