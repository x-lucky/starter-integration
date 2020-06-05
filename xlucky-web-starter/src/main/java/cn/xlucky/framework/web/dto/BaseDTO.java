package cn.xlucky.framework.web.dto;


import java.io.Serializable;


/**
 * 请求响应父类
 * @author xlucky
 * @date 2020/6/4
 * @version 1.0.0
 */
public abstract class BaseDTO implements Serializable {
    @Override
    public String toString() {
        return "BaseDTO{}";
    }
}