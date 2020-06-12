package cn.xlucky.framework.redis.lock;


/**
 * 锁
 *
 * @author xlucky
 * @date 2020/6/9
 * @version 1.0.0
 */
public interface ICacheLockManager {
    /**
     * 获取lock,立刻返回结果，不会阻塞
     * <p>
     *
     * @param key
     * @param tokenVersion
     * @param timeout s
     * @return boolean
     * @author xlucky
     * @date 2020/6/9 15:22
     * @version 1.0.0
     */
    boolean tryLock(String key, String tokenVersion, int timeout);

    /**
     * 获取lock
     * <p>
     *
     * @param key
     * @param tokenVersion
     * @return boolean
     * @author xlucky
     * @date 2020/6/9 15:22
     * @version 1.0.0
     */
    boolean removeLock(String key, String tokenVersion);
}
