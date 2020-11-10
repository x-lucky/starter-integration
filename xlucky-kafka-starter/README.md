## kafka

使用：  
导入依赖  

```
<dependency>
	<groupId>cn.xlucky</groupId>
	<artifactId>xlucky-kafka-starter</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```
yml配置
```
spring:
  kafka:
    #kafka集群连接
    bootstrap-servers: localhost:9092
    consumer:
      # 自动提交的时间间隔 在spring boot 2.X 版本中这里采用的是值的类型为Duration 需要符合特定的格式，如1S,1M,2H,5D
      auto-commit-interval: 1S
      #这个参数指定了当消费者第一次读取分区或者无offset时拉取那个位置的消息，可以取值为latest（从最新的消息开始消费）,earliest（从最老的消息开始消费）,none（如果无offset就抛出异常）
      auto-offset-reset: earliest
      # 是否自动提交偏移量，默认值是true,为了避免出现重复数据和数据丢失，可以把它设置为false,然后手动提交偏移量
      enable-auto-commit: true
      # 序列化方式
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      # 每次批量发送消息的数量
      batch-size: 16384
      buffer-memory: 33554432
      # 重试次数
      retries: 0
      # acks=0 ： 生产者在成功写入消息之前不会等待任何来自服务器的响应。
      # acks=1 ： 只要集群的首领节点收到消息，生产者就会收到一个来自服务器成功响应。
      # acks=all ：只有当所有参与复制的节点全部收到消息时，生产者才会收到一个来自服务器的成功响应。
      acks: 1
      # 序列化方式
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
  listener:
    #listner负责ack，每调用一次，就立即commit。结合consumer.enable-auto-commit:true 使用
    ack-mode: manual_immediate
    # 在侦听器容器中运行的线程数。和分区数要一致。KafkaListener也可设置
    concurrency: 5
```





