package cn.xlucky.framework.common.dto;

import cn.xlucky.framework.common.dto.enums.ResultCodeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;


/**
 * 响应体
 * @author xlucky
 * @date 2020/6/4
 * @version 1.0.0
 */
@ApiModel(
    description = "响应结果"
)
@JsonInclude(Include.NON_NULL)
public class RestResult<T> implements Serializable {
    @ApiModelProperty(
        notes = "状态码；200：成功， 非200：失败"
    )
    protected int code;
    @ApiModelProperty(
        notes = "响应消息"
    )
    protected String message;
    @ApiModelProperty(
        notes = "时间戳"
    )
    protected Long timestamp;
    @ApiModelProperty(
        notes = "返回数据"
    )
    protected T data;

    public RestResult() {
        this.code = ResultCodeEnum.OK.getCode();
        this.message = ResultCodeEnum.OK.getMessage();
        this.timestamp = System.currentTimeMillis();
    }

    private RestResult(RestResult.Builder<T> builder) {
        this.timestamp = System.currentTimeMillis();
        this.code = builder.code;
        this.message = builder.message;
        this.data = builder.data;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public RestResult<T> code(int code) {
        this.code = code;
        return this;
    }

    public RestResult<T> message(String message) {
        this.message = message;
        return this;
    }

    public RestResult<T> putTimestamp() {
        this.timestamp = System.currentTimeMillis();
        return this;
    }

    public RestResult<T> data(T data) {
        this.data = data;
        return this;
    }

    public static class Builder<T> {
        private int code;
        private String message;
        private T data;

        public Builder() {
        }

        public RestResult.Builder code(int code) {
            this.code = code;
            return this;
        }

        public RestResult.Builder message(String message) {
            this.message = message;
            return this;
        }

        public RestResult.Builder data(T data) {
            this.data = data;
            return this;
        }

        public RestResult<T> build() {
            RestResult<T> restResult = new RestResult(this);
            return restResult;
        }
    }
}