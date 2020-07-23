package cn.xlucky.framework.dubbo.exception;

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
import org.apache.dubbo.rpc.service.GenericService;


/**
 * ConsumerExceptionFilter
 * @author xlucky
 * @date 2020/7/2
 * @version 1.0.0
 */
@Activate(
    group = {CommonConstants.CONSUMER},
    order = 1
)
@Slf4j
public class ConsumerExceptionFilter extends AbstractExceptionInterceptor implements Filter {

    public ConsumerExceptionFilter() {
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) {
        Result result = null;

        try {
            result = invoker.invoke(invocation);
            if (result.hasException() && GenericService.class != invoker.getInterface()) {
                Throwable exception = result.getException();
                return this.handleRpcException(exception, invocation);
            }
        } catch (Throwable var5) {
            return this.handleRpcException(var5, invocation);
        }

        if (result.getValue() == null) {
            throw new BusinessException(ResultCodeEnum.SYSTEM_EXCEPTION.getCode());
        } else {
            return result;
        }
    }

    private Result handleRpcException(Throwable exception, Invocation invocation) {
        RestResult restResult = this.handle(exception);
        return AsyncRpcResult.newDefaultAsyncResult(restResult, null, invocation);
    }

    public RestResult handle(Throwable exception) {
        RestResult.Builder resultBuilder = new RestResult.Builder();;
        if (exception instanceof RpcException) {
            log.error("调用dubbo远程服务异常,e:", exception);
            BusinessException be = new BusinessException(ResultCodeEnum.RPC_EXCEPTION.getCode());
            resultBuilder.code(be.getExCode()).message(be.getExDesc());
        } else if (exception instanceof BusinessException) {
            BusinessException be = (BusinessException)exception;
            log.warn("业务异常; {}", be.getMessage());
            resultBuilder.code(be.getExCode()).message(be.getExDesc());
        } else {
            log.error("调用dubbo远程服务系统异常,e:", exception);
            resultBuilder.code(ResultCodeEnum.SYSTEM_EXCEPTION.getCode()).message(exception.getMessage());
        }
        return resultBuilder.build();
    }

}