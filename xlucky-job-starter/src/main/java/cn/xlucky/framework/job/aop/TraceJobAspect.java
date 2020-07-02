package cn.xlucky.framework.job.aop;

import cn.xlucky.framework.common.log.LogTracing;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;



/**
 * 类说明
 * @author xlucky
 * @date 2020/6/12
 * @version 1.0.0
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class TraceJobAspect {
    public TraceJobAspect() {
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Around("@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
    public Object traceBackgroundThread(ProceedingJoinPoint pjp) throws Throwable {
        Object var2;
        try {
            String traceId = LogTracing.start();
            XxlJobLogger.log("{}-jobHandler name:{}", traceId, this.getClass().getName());
            var2 = pjp.proceed();
        } finally {
            LogTracing.end();
        }

        return var2;
    }
}