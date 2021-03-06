package cn.xlucky.framework.common.test;

import com.google.common.util.concurrent.RateLimiter;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author xlucky
 */
public class MultiThreadTestUtils {


    /**
     * 按任务数来测试并发
     *
     * @param poolSize  线程池大小
     * @param queueSize 任务队列大小
     * @param tasks     任务列表
     * @return
     * @throws InterruptedException
     */
    public static TestResult execute(int poolSize, int queueSize, Runnable... tasks) throws InterruptedException {
        TestResult testResult = TestResult.build();
        testResult.setWorkThreads(poolSize);
        int taskCount = tasks.length;
        CountDownLatch executeLatch = new CountDownLatch(taskCount);
        CountDownLatch finishLatch = new CountDownLatch(taskCount);
        ExecutorService executor = newExecutorService(poolSize, queueSize, testResult, finishLatch);

        AtomicInteger taskCounter = new AtomicInteger(0);
        AtomicLong actualTotalTime = new AtomicLong(0);
        testResult.setTaskCounter(taskCounter);
        testResult.setActualTotalTime(actualTotalTime);

        SystemMonitor.start(testResult);
        long s = System.currentTimeMillis();
        for (int i = 0; i < taskCount; i++) {
            Runnable target = tasks[i];
            executor.execute(() -> {
                long st = System.currentTimeMillis();
                try {
                    executeLatch.await();
                    target.run();
                } catch (Throwable e) {
                    testResult.addException(e);
                } finally {
                    finishLatch.countDown();
                    taskCounter.incrementAndGet();
                    long et = System.currentTimeMillis();
                    long usedTime = et - st;
                    actualTotalTime.getAndAdd(usedTime);
                }
            });
            executeLatch.countDown();
        }
        finishLatch.await();
        SystemMonitor.stop();
        executor.shutdown();
        return testResult;
    }

    public static TestResult execute(int poolSize, int queueSize, int taskCount, Runnable target) throws InterruptedException {
        TestResult testResult = TestResult.build();
        testResult.setWorkThreads(poolSize);
        CountDownLatch executeLatch = new CountDownLatch(taskCount);
        CountDownLatch finishLatch = new CountDownLatch(taskCount);
        ExecutorService executor = newExecutorService(poolSize, queueSize, testResult, finishLatch);

        AtomicInteger taskCounter = new AtomicInteger(0);
        AtomicLong actualTotalTime = new AtomicLong(0);
        testResult.setTaskCounter(taskCounter);
        testResult.setActualTotalTime(actualTotalTime);

        SystemMonitor.start(testResult);
        long s = System.currentTimeMillis();
        for (int i = 0; i < taskCount; i++) {
            executor.execute(() -> {
                long st = System.currentTimeMillis();
                try {
                    executeLatch.await();
                    target.run();
                } catch (Throwable e) {
                    testResult.addException(e);
                } finally {
                    finishLatch.countDown();
                    taskCounter.incrementAndGet();
                    long et = System.currentTimeMillis();
                    long usedTime = et - st;
                    actualTotalTime.getAndAdd(usedTime);
                }
            });
            executeLatch.countDown();
        }
        finishLatch.await();
        SystemMonitor.stop();
        executor.shutdown();
        return testResult;
    }


    /**
     * 指定执行时长
     *
     * @param poolSize
     * @param queueSize
     * @param executeTime 指定测试时长 单位ms
     * @param target
     * @return
     * @throws InterruptedException
     */
    public static TestResult execute(int poolSize, int queueSize, long executeTime, Runnable target) throws InterruptedException {
        return execute(poolSize, queueSize, executeTime, 0, target);
    }

    /**
     * 指定执行时长
     *
     * @param poolSize
     * @param queueSize
     * @param executeTime 指定测试时长 单位ms
     * @param target
     * @param submitTps   任务提交速率，每秒提效的任务数
     * @return
     * @throws InterruptedException
     */
    public static TestResult execute(int poolSize, int queueSize, long executeTime, int submitTps, Runnable target) throws InterruptedException {
        TestResult testResult = TestResult.build();
        testResult.setWorkThreads(poolSize);
        testResult.setSubmitTps(submitTps);
        ExecutorService executor = newExecutorService(poolSize, queueSize, testResult, null);
        AtomicInteger taskCounter = new AtomicInteger(0);
        AtomicLong actualTotalTime = new AtomicLong(0);
        testResult.setTaskCounter(taskCounter);
        testResult.setActualTotalTime(actualTotalTime);
        SystemMonitor.start(testResult);
        Timer timer = new Timer();
        final Boolean[] run = {true};
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //停目执行任务
                run[0] = false;
            }
        }, executeTime);

        RateLimiter limiter = RateLimiter.create(submitTps);
        while (run[0]) {
            limiter.acquire();
            submitTask(target, testResult, executor, taskCounter, actualTotalTime);
        }
        SystemMonitor.stop();
        executor.shutdown();
        return testResult;
    }


    private static void submitTask(Runnable target, TestResult testResult, ExecutorService executor, AtomicInteger taskCounter, AtomicLong actualTotalTime) {
        executor.execute(() -> {
            long st = System.currentTimeMillis();
            try {
                target.run();
            } catch (Throwable throwable) {
                testResult.addException(throwable);
            } finally {
                taskCounter.incrementAndGet();
                long et = System.currentTimeMillis();
                long usedTime = et - st;
                actualTotalTime.getAndAdd(usedTime);
            }
        });
    }


    private static ExecutorService newExecutorService(int poolSize, int queueSize, TestResult result, CountDownLatch finishLatch) {
        LinkedBlockingQueue taskQueue = new LinkedBlockingQueue<>(queueSize);
        result.setTaskQueue(taskQueue);
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(poolSize, poolSize, 2, TimeUnit.SECONDS, taskQueue, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, "TestThread-" + threadIndex.getAndIncrement());
            }
        }, new CountRejectedExecutionHandler(result, finishLatch));
        return poolExecutor;
    }

    static class CountRejectedExecutionHandler implements RejectedExecutionHandler {

        private CountDownLatch finishLatch;
        private TestResult result;

        public CountRejectedExecutionHandler(TestResult result, CountDownLatch finishLatch) {
            this.result = result;
            this.finishLatch = finishLatch;
        }

        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
            result.getRejectCount().getAndIncrement();
            if (finishLatch != null) {
                finishLatch.countDown();
            }
        }
    }
}
