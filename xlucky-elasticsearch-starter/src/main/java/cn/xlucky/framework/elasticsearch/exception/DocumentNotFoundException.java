package cn.xlucky.framework.elasticsearch.exception;


/**
 * 文档异常
 * @author xlucky
 * @date 2020/5/29
 * @version 1.0.0
 */
public class DocumentNotFoundException extends Exception {
    public DocumentNotFoundException() {
    }

    public DocumentNotFoundException(String message) {
        super(message);
    }
}
