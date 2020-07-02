package cn.xlucky.framework.dubbo.log;

import cn.xlucky.framework.common.constant.CommonConstant;
import cn.xlucky.framework.common.log.LogTracing;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.core.Ordered;

/**
 * 消费者日志
 * @author xlucky
 * @date 2020/6/30
 * @version 1.0.0
 */
@Activate(
    group = {CommonConstants.CONSUMER},
    order = Ordered.HIGHEST_PRECEDENCE
)
public class DubboConsumerLogTraceFilter implements Filter {
    public DubboConsumerLogTraceFilter() {
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        invocation.setAttachment(CommonConstant.LOG_TRACE_ID, LogTracing.getTraceId());
        return invoker.invoke(invocation);
    }
}
