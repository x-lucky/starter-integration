package cn.xlucky.framework.common.dto.enums;


/**
 * 响应体枚举
 * @author xlucky
 * @date 2020/6/4
 * @version 1.0.0
 */
public enum ResultCodeEnum {
    /**
     * 成功失败枚举
     */
    OK(200, "执行成功！"),
    NO(201, "执行失败！"),
    SYSTEM_EXCEPTION(5000, "系统异常！"),
    RPC_EXCEPTION(5001, "远程调用异常"),
    BAD_REQUEST_PARAMS(10001, "缺少必要的请求参数！"),
    BAD_REQUEST_MEDIA_TYPE(10002, "HTTP Headers MediaType 错误！"),
    PARAM_BIND_EXCEPTION(10003, "参数绑定异常，请检查表单填写的数据！"),
    PARSE_JSON_EXCEPTION(10004, " JSON转化异常！");

    private int code;
    private String message;

    private ResultCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
