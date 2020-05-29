package cn.xlucky.framework.elasticsearch.exception;

/**
 * 版本异常
 * @author xlucky
 * @date 2020/5/29
 * @version 1.0.0
 */
public class VersionConflictException extends Exception {
    public VersionConflictException() {
    }

    public VersionConflictException(String message) {
        super(message);
    }
}