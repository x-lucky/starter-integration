## 实现热更新配置

使用：  
导入依赖  

```
使用版本:
es测试版本:6.3.0
<dependency>
	<groupId>cn.xlucky</groupId>
	<artifactId>xlucky-elasticsearch-starter</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```
yml配置
```
elasticsearch:
    host: http://127.0.0.1:9200/,http://127.0.0.1:9300/
    maxTotalConnection: 300
    timeout: 10000
```





