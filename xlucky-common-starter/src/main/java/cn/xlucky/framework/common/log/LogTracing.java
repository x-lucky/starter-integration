package cn.xlucky.framework.common.log;

import java.util.UUID;

import cn.xlucky.framework.common.constant.CommonConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;


/**
 * 日志跟踪
 * @author xlucky
 * @date 2020/6/30
 * @version 1.0.0
 */
public class LogTracing {

    private LogTracing() {
    }

    public static String start() {
        String traceId = MDC.get(CommonConstant.LOG_TRACE_ID);
        if (traceId != null) {
            return traceId;
        } else {
            traceId = generateTraceId();
            MDC.put(CommonConstant.LOG_TRACE_ID, traceId);
            return traceId;
        }
    }

    public static String start(String traceId) {
        if (StringUtils.isEmpty(traceId)) {
            traceId = generateTraceId();
        }
        MDC.put(CommonConstant.LOG_TRACE_ID, traceId);
        return traceId;
    }

    public static String getTraceId() {
        return MDC.get(CommonConstant.LOG_TRACE_ID);
    }

    public static void end() {
        MDC.remove(CommonConstant.LOG_TRACE_ID);
    }

    private static String generateTraceId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}