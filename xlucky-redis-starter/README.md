## redis再封装

使用：  
导入依赖  

```
<dependency>
	<groupId>cn.xlucky</groupId>
	<artifactId>xlucky-redis-starter</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```
yml配置
```
spring:
    redis:
        cluster:
            nodes: 127.0.0.1:6379,127.0.0.1:6389
        lettuce:
            pool:
                max-active: 50
                max-idle: 20
                min-idle: 5
        timeout: 5000

```

锁使用示例
```
    @Autowired
    private ICacheLockManager cacheLockManager;

    public void demoLock() {
        try {
            boolean lock = cacheLockManager.getLock("key", "value", 5);
            if (lock) {
                //do something
            }
        } finally {
            boolean b = cacheLockManager.removeLock("key", "value");
        }
    }
```






