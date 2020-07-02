package cn.xlucky.framework.dubbo.execption;

import cn.xlucky.framework.common.dto.RestResult;

public abstract class AbstractExceptionInterceptor{

    public AbstractExceptionInterceptor() {
    }

    protected Throwable findRootCauseException(Throwable throwable) {
        Throwable cause = throwable.getCause();
        return cause == null ? throwable : this.findRootCauseException(cause);
    }
}