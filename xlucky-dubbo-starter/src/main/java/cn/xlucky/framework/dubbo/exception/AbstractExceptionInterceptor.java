package cn.xlucky.framework.dubbo.exception;

public abstract class AbstractExceptionInterceptor{

    public AbstractExceptionInterceptor() {
    }

    protected Throwable findRootCauseException(Throwable throwable) {
        Throwable cause = throwable.getCause();
        return cause == null ? throwable : this.findRootCauseException(cause);
    }
}