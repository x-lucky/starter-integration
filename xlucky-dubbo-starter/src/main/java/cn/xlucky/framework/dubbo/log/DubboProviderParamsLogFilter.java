package cn.xlucky.framework.dubbo.log;

import cn.xlucky.framework.common.concurrent.XluckyThreadFactory;
import cn.xlucky.framework.common.constant.CommonConstant;
import cn.xlucky.framework.common.log.LogTracing;
import cn.xlucky.framework.dubbo.dto.ParamsAspectEntity;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;

/**
 * 生产者日志
 * @author xlucky
 * @date 2020/6/30
 * @version 1.0.0
 */
@Activate(
    group = {CommonConstants.PROVIDER},
    order = Ordered.HIGHEST_PRECEDENCE
)
public class DubboProviderParamsLogFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DubboProviderParamsLogFilter.class);
    @Value("${spring.application.name:unknown}")
    private String applicationName;
    private static ExecutorService PARAMS_LOG_THREAD_POOL = null;

    public DubboProviderParamsLogFilter() {
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Result var5;
        try {
            LogTracing.start(invocation.getAttachment(CommonConstant.LOG_TRACE_ID));
            ParamsAspectEntity paramsAspectEntity = new ParamsAspectEntity();
            paramsAspectEntity.setStartTime(System.currentTimeMillis());
            paramsAspectEntity.setMethodStr(invoker.getInterface().getSimpleName() + "." + invocation.getMethodName());
            this.in(invocation, paramsAspectEntity);
            Result result = invoker.invoke(invocation);
            this.out(result, paramsAspectEntity);
            var5 = result;
        } finally {
            MDC.clear();
        }

        return var5;
    }

    private void in(Invocation invocation, ParamsAspectEntity paramsAspectEntity) {
        Map<String, String> attachments = invocation.getAttachments();
        StringBuffer attachmentsBuffer = new StringBuffer();
        if (attachments != null) {
            attachments.forEach((key, value) -> {
                attachmentsBuffer.append("\n").append(key).append(" : ").append(value);
            });
        }

        LOGGER.info("{}\nDubbo attachments : {}", paramsAspectEntity.getMethodStr(), attachmentsBuffer.toString());

        try {
            InParameterPrinter inParameterPrinter = new InParameterPrinter(this.applicationName, paramsAspectEntity
                    , false, invocation, LogTracing.getTraceId());
            PARAMS_LOG_THREAD_POOL.submit(inParameterPrinter);
        } catch (Throwable var6) {
            LOGGER.error(var6.getMessage(), var6);
        }

    }

    private void out(Result result, ParamsAspectEntity paramsAspectEntity) {
        try {
            paramsAspectEntity.setEndTime(System.currentTimeMillis());
            OutParameterPrinter outParameterPrinter = new OutParameterPrinter(this.applicationName, paramsAspectEntity, false, result.getValue(), LogTracing.getTraceId());
            PARAMS_LOG_THREAD_POOL.submit(outParameterPrinter);
        } catch (Throwable var4) {
            LOGGER.error(var4.getMessage(), var4);
        }

    }

    static {
        int coreCount = Runtime.getRuntime().availableProcessors();
        int poolSize = coreCount / 2;
        if (poolSize <= 0) {
            poolSize = 1;
        }

        PARAMS_LOG_THREAD_POOL = new ThreadPoolExecutor(poolSize, coreCount * 2, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue(coreCount * 5), new XluckyThreadFactory("DubboParams"), new CallerRunsPolicy());
    }
}