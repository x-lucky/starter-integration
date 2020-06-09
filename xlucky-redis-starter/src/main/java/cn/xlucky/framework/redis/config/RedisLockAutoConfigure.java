package cn.xlucky.framework.redis.config;

import cn.xlucky.framework.redis.lock.ICacheLockManager;
import cn.xlucky.framework.redis.lock.SimpleRedisLockManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import org.springframework.data.redis.core.StringRedisTemplate;




/**
 * 配置锁
 * @author xlucky
 * @date 2020/6/9
 * @version 1.0.0
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RedisLockAutoConfigure {

    @Bean
    @ConditionalOnMissingBean
    public ICacheLockManager redisCacheLockManager(StringRedisTemplate redisTemplate) {
        ICacheLockManager redisCacheLockManager = new SimpleRedisLockManager(redisTemplate);
        return redisCacheLockManager;
    }

}