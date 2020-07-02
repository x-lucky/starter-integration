package cn.xlucky.framework.dubbo.execption;

import cn.xlucky.framework.common.dto.RestResult;
import cn.xlucky.framework.common.dto.enums.ResultCodeEnum;
import cn.xlucky.framework.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;


/**
 * provider
 * @author xlucky
 * @date 2020/7/2
 * @version 1.0.0
 */
@Activate(
    group = {CommonConstants.PROVIDER},
    order = 10
)
@Slf4j
public class ProviderExceptionHandler extends AbstractExceptionInterceptor implements Filter {

    public ProviderExceptionHandler() {
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Exception exception = null;
        Result result = null;

        try {
            result = invoker.invoke(invocation);
        } catch (Exception var8) {
            exception = var8;
        }

        RestResult exceptionResult = null;
        if (exception != null) {
            exceptionResult = this.handle(exception);
        } else if (result.getException() != null) {
            exceptionResult = this.handle(result.getException());
        }

        if (exceptionResult != null) {
            AsyncRpcResult asyncRpcResult = AsyncRpcResult.newDefaultAsyncResult(exceptionResult, exception, invocation);
            result = asyncRpcResult;
        }

        return result;
    }

    public RestResult handle(Throwable throwable) {
        Throwable rootCase = this.findRootCauseException(throwable);
        RestResult.Builder resultBuilder = new RestResult.Builder();
        if (rootCase instanceof BusinessException) {
            BusinessException businessException = (BusinessException)rootCase;
            resultBuilder.code(businessException.getExCode()).message(businessException.getMessage());
            if (businessException.getExCode() == ResultCodeEnum.SYSTEM_EXCEPTION.getCode()) {
                log.error("系统异常,e: ",rootCase);
            } else {
                log.warn("业务异常; {}", businessException.getMessage());
            }
        } else {
            log.error("系统异常,e:", rootCase);
            resultBuilder.code(ResultCodeEnum.SYSTEM_EXCEPTION.getCode()).message(rootCase.getMessage());
        }
        return resultBuilder.build();
    }
}
