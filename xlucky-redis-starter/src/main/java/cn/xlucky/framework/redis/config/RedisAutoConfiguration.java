package cn.xlucky.framework.redis.config;

import cn.xlucky.framework.redis.RedisUtils;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import java.time.Duration;


/**
 * 配置类
 *
 * @author xlucky
 * @date 2020/6/9
 * @version 1.0.0
 */
@Configuration
public class RedisAutoConfiguration {
    @Autowired
    private RedisProperties redisProperties;

    @Bean
    RedisUtils redisUtils() {
        return new RedisUtils();
    }

    @Value("${spring.redis.cluster.topology-refresh-period:5}")
    private long topologyRefreshPeriod = 5L;


    /**
     * 为RedisTemplate配置Redis连接工厂实现
     * LettuceConnectionFactory实现了RedisConnectionFactory接口
     * UVPV用Redis
     *
     * @return 返回LettuceConnectionFactory
     */
    @Bean(destroyMethod = "destroy")
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisProperties.Cluster clusterProperties = this.redisProperties.getCluster();
        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(clusterProperties.getNodes());
        if (clusterProperties.getMaxRedirects() != null) {
            clusterConfiguration.setMaxRedirects(clusterProperties.getMaxRedirects());
        }
        if (this.redisProperties.getPassword() != null) {
            clusterConfiguration.setPassword(RedisPassword.of(this.redisProperties.getPassword()));
        }
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(clusterConfiguration, getLettuceClientConfiguration(genericObjectPoolConfig()));
        //每次调用redis校验
        lettuceConnectionFactory.setValidateConnection(true);
        //每次开启一个连接
        lettuceConnectionFactory.setShareNativeConnection(false);
        return new LettuceConnectionFactory(clusterConfiguration, getLettuceClientConfiguration(genericObjectPoolConfig()));
    }

    /**
     * 连接池配置
     * <p>
     *
     * @return
     * @author xlucky
     * @date 2020/6/9 15:15
     * @version 1.0.0
     */
    public GenericObjectPoolConfig genericObjectPoolConfig() {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        RedisProperties.Pool pool = redisProperties.getLettuce().getPool();
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxTotal(pool.getMaxActive());
        if (pool.getMaxWait() != null) {
            poolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
        }
        return poolConfig;
    }

    /**
     * 配置LettuceClientConfiguration 包括线程池配置和安全项配置
     * 见 https://github.com/lettuce-io/lettuce-core/wiki/Client-options#periodic-cluster-topology-refresh
     * @param genericObjectPoolConfig common-pool2线程池
     * @return lettuceClientConfiguration
     */
    private LettuceClientConfiguration getLettuceClientConfiguration(GenericObjectPoolConfig genericObjectPoolConfig) {
        ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                // 周期刷新 默认五秒
                .enablePeriodicRefresh(Duration.ofSeconds(this.topologyRefreshPeriod))
                .build();
        return LettucePoolingClientConfiguration.builder()
                .poolConfig(genericObjectPoolConfig)
                .clientOptions(ClusterClientOptions.builder().topologyRefreshOptions(topologyRefreshOptions).build())
                //将appID传入连接，方便Redis监控中查看
//                .clientName(appName + "_lettuce")
                .build();
    }

}
