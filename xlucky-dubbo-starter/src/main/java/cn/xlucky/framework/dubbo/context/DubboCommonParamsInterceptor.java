package cn.xlucky.framework.dubbo.context;

import cn.xlucky.framework.common.constant.CommonConstant;
import cn.xlucky.framework.common.dto.BaseDto;
import cn.xlucky.framework.common.log.LogTracing;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;


/**
 * DubboCommonParamsInterceptor
 * @author xlucky
 * @date 2020/7/2
 * @version 1.0.0
 */
@Activate(
    group = {"consumer"},
    order = -10005
)
public class DubboCommonParamsInterceptor implements Filter {
    public DubboCommonParamsInterceptor() {
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContext context = RpcContext.getContext();

        Result var4;
        try {
            context.setAttachment(CommonConstant.LOG_TRACE_ID, LogTracing.getTraceId());
            this.setRequestParamOperator(invocation.getArguments());
            var4 = invoker.invoke(invocation);
        } finally {
            context.removeAttachment(CommonConstant.LOG_TRACE_ID);
        }

        return var4;
    }

    private void setRequestParamOperator(Object[] args) {
        if (args != null && args.length != 0) {
            Object[] var2 = args;
            int var3 = args.length;
            for(int var4 = 0; var4 < var3; ++var4) {
                Object arg = var2[var4];
                if (arg instanceof BaseDto) {
                    BaseDto var6 = (BaseDto)arg;
                }
            }
        }
    }

}