## mybatis 读写分离 分页

使用：  
导入依赖

```
<dependency>
	<groupId>cn.xlucky</groupId>
	<artifactId>xlucky-mybatis-starter</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```
yml配置
```
hikari:
  datasource:
    masterJdbcUrl: jdbc:mysql://127.0.0.1:3306/user?useSSL=false&useUnicode=true&characterEncoding=utf-8&serverTimezone=CTT
    masterUsername: root
    masterPassword: p@ssw0rd
    slaveJdbcUrl: jdbc:mysql://127.0.0.1:3306/user?useSSL=false&useUnicode=true&characterEncoding=utf-8&serverTimezone=CTT
    slavePassword: root
    slaveUsername: p@ssw0rd
    cachePrepStmts: true
    connectionTimeout: 60000
    maximumPoolSize: 50
    prepStmtCacheSize: 250
    prepStmtCacheSqlLimit: 2048
    useServerPrepStmts: true
    driverClassName: com.mysql.jdbc.Driver
```
目录结构
java
...entity
...mapper.master
         .slave
resources
...mapper.master
         .slave



