package cn.xlucky.framework.dubbo.log;

import cn.xlucky.framework.common.aop.SensitivityJsonFilter;
import cn.xlucky.framework.common.log.LogTracing;
import cn.xlucky.framework.dubbo.dto.ParamsAspectEntity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 输出日志
 * @author xlucky
 * @date 2020/6/30
 * @version 1.0.0
 */
public class OutParameterPrinter implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(OutParameterPrinter.class);
    private ParamsAspectEntity paramsAspectEntity;
    private String applicationName;
    private Object returnValue;
    private SerializeFilter sensitivityJSONFilter = new SensitivityJsonFilter();
    private boolean closeParameterLog;
    private String traceId;

    public OutParameterPrinter(String applicationName, ParamsAspectEntity paramsAspectEntity, boolean closeParameterLog, Object returnValue, String traceId) {
        this.paramsAspectEntity = paramsAspectEntity;
        this.closeParameterLog = closeParameterLog;
        this.applicationName = applicationName;
        this.returnValue = returnValue;
        this.traceId = traceId;
    }

    @Override
    public void run() {
        try {
            LogTracing.start(traceId);
            StringBuffer returnValueBuffer = new StringBuffer();
            if (this.closeParameterLog) {
                returnValueBuffer.append("Method closes the out parameters log.");
            } else if (this.returnValue != null) {
                try {
                    returnValueBuffer.append(JSON.toJSONString(this.returnValue, this.sensitivityJSONFilter, new SerializerFeature[0]));
                } catch (Exception var7) {
                    returnValueBuffer.append(this.returnValue.toString());
                }
            }

            long cost = this.paramsAspectEntity.getEndTime() - this.paramsAspectEntity.getStartTime();
            LOGGER.info("{}\nDubbo out ({}ms) : {}", new Object[]{this.paramsAspectEntity.getMethodStr(), cost, returnValueBuffer.toString()});
        } finally {
            LogTracing.end();
        }

    }
}