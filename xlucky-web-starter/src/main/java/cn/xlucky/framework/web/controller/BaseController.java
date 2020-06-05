package cn.xlucky.framework.web.controller;

import cn.xlucky.framework.web.dto.RestResult;
import cn.xlucky.framework.web.dto.enums.ResultCodeEnum;


/**
 * BaseController
 * @author xlucky
 * @date 2020/6/4
 * @version 1.0.0
 */
public abstract class BaseController {
    public BaseController() {
    }

    public <T> RestResult<T> success() {
        return this.success(null);
    }

    public <T> RestResult<T> success(T data) {
        return this.success(ResultCodeEnum.OK.getMessage(), data);
    }

    public <T> RestResult<T> fail(String message) {
        return fail(ResultCodeEnum.NO.getCode(),message);
    }

    public <T> RestResult<T> success(String message, T data) {
        return new RestResult.Builder()
                .code(ResultCodeEnum.OK.getCode())
                .message(message)
                .data(data)
                .build();
    }

    public <T> RestResult<T> fail(int code, String message) {
        return new RestResult.Builder()
                .code(code)
                .message(message)
                .build();
    }
}