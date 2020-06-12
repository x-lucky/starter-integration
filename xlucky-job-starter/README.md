## xxl-job starter

使用：  
导入依赖  

```
<dependency>
	<groupId>cn.xlucky</groupId>
	<artifactId>xlucky-job-starter</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```
yml配置
```
#除了port，其他配置建议放在公共配置
xxl:
  job:
    adminAddresses: http://localhost:8080/xxl-job-admin
    appName: ${spring.application.name}-dev
    logPath: logs/xxl-job/jobhandler
    port: 8888
    logRetentionDays : 30
```

```
示例
在cn.xlucky.framework.**.jobhandler包下

    @XxlJob(value = "testJobHandler")
	public ReturnT<String> execute(String param) throws Exception {
		log.info("TestJobHandler ------------------------{}",param);
		return ReturnT.SUCCESS;
	}
```






