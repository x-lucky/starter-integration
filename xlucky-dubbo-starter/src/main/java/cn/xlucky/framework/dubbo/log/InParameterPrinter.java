package cn.xlucky.framework.dubbo.log;

import cn.xlucky.framework.common.aop.SensitivityJsonFilter;
import cn.xlucky.framework.common.log.LogTracing;
import cn.xlucky.framework.dubbo.dto.ParamsAspectEntity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.dubbo.rpc.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 输入日志
 * @author xlucky
 * @date 2020/6/30
 * @version 1.0.0
 */
public class InParameterPrinter implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(InParameterPrinter.class);
    private ParamsAspectEntity paramsAspectEntity;
    private String applicationName;
    private Invocation invocation;
    private SerializeFilter sensitivityJSONFilter = new SensitivityJsonFilter();
    private boolean closeParameterLog;
    private String traceId;

    public InParameterPrinter(String applicationName, ParamsAspectEntity paramsAspectEntity, boolean closeParameterLog, Invocation invocation, String traceId) {
        this.paramsAspectEntity = paramsAspectEntity;
        this.closeParameterLog = closeParameterLog;
        this.invocation = invocation;
        this.applicationName = applicationName;
        this.traceId = traceId;
    }

    @Override
    public void run() {
        try {
            LogTracing.start(this.traceId);
            Object[] arguments = this.invocation.getArguments();
            StringBuffer argumentsBuffer = new StringBuffer();
            if (this.closeParameterLog) {
                argumentsBuffer.append("Method closes the in parameters log.");
            } else if (arguments != null && arguments != null) {
                Object[] var3 = arguments;
                int var4 = arguments.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    Object argument = var3[var5];

                    try {
                        argumentsBuffer.append(JSON.toJSONString(argument, this.sensitivityJSONFilter, new SerializerFeature[0])).append(", ");
                    } catch (Exception var11) {
                        argumentsBuffer.append(argument.toString()).append(", ");
                        LOGGER.error(var11.getMessage(), var11);
                    }
                }
            }

            LOGGER.info("{}\nDubbo in : {}", this.paramsAspectEntity.getMethodStr(), argumentsBuffer.toString());
        } finally {
            LogTracing.end();
        }

    }
}