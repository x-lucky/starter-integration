package cn.xlucky.framework.job.aop;

import cn.xlucky.framework.constant.CommonConstant;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.UUID;


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
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            MDC.put(CommonConstant.LOG_UUID_MDC_KEY, uuid);
            XxlJobLogger.log("{}-jobHandler name:{}", uuid, this.getClass().getName());
            var2 = pjp.proceed();
        } finally {
            MDC.remove(CommonConstant.LOG_UUID_MDC_KEY);
        }

        return var2;
    }
}