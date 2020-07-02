package cn.xlucky.framework.common.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 类说明
 * @author xlucky
 * @date 2020/6/30
 * @version 1.0.0
 */
public class XluckyThreadFactory implements ThreadFactory {
    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    public static final ExecutorService THREAD_POOL;

    public XluckyThreadFactory() {
        SecurityManager var1 = System.getSecurityManager();
        this.group = var1 != null ? var1.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = "xluck" + POOL_NUMBER.getAndIncrement() + "-thread-";
    }

    public XluckyThreadFactory(String name) {
        SecurityManager var1 = System.getSecurityManager();
        this.group = var1 != null ? var1.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = name + POOL_NUMBER.getAndIncrement() + "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != 5) {
            t.setPriority(5);
        }
        return t;
    }

    static {
        THREAD_POOL = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 2, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue(Runtime.getRuntime().availableProcessors() * 100), new XluckyThreadFactory());
    }
}