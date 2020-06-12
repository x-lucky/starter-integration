package cn.xlucky.framework.redis.lock;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;


/**
 * 简单的redisLock 没有看门狗机制
 * @author xlucky
 * @date 2020/6/8
 * @version 1.0.0
 */
public class SimpleRedisLockManager implements ICacheLockManager {
    public static final DefaultRedisScript REMOVE_LOCK_LUA_SCRIPT = new DefaultRedisScript("if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return -1 end", Long.class);
    public static final DefaultRedisScript GET_LOCK_LUA_SCRIPT = new DefaultRedisScript("if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then return redis.call('pexpire', KEYS[1], ARGV[2]) else return 0 end", Long.class);
    private StringRedisTemplate redisTemplate;

    public SimpleRedisLockManager(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean tryLock(String key, String tokenVersion, int timeout) {
        List<String> keys = new ArrayList();
        keys.add(key);
        Object result = this.redisTemplate.execute(GET_LOCK_LUA_SCRIPT, keys, new Object[]{tokenVersion, String.valueOf(timeout)});
        return String.valueOf(1).equals(result.toString());
    }

    @Override
    public boolean removeLock(String key, String tokenVersion) {
        List<String> keys = new ArrayList();
        keys.add(key);
        Object result = this.redisTemplate.execute(REMOVE_LOCK_LUA_SCRIPT, keys, new Object[]{tokenVersion});
        return String.valueOf(1).equals(result.toString());
    }
}