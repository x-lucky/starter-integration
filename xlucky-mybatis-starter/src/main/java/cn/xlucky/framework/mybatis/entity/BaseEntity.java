package cn.xlucky.framework.mybatis.entity;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * BaseEntity
 * @author xlucky
 * @date 2020/6/4
 * @version 1.0.0
 */
@JsonIgnoreProperties({"pk"})
@Slf4j
public abstract class BaseEntity implements Serializable {

    public BaseEntity() {
    }
    public static <T> T valueOf(String text) {
        T t = (T) JSON.parseObject(text);
        return t;
    }

    public static <T> T valueOf(Map<String, String> entityMap) {
        return null;
    }

    public String toJSONString() {
        return JSON.toJSONString(this);
    }

    public static String toJSON(Object o) {
        if (o != null) {
            JSON.toJSONString(o);
        }
        return "";
    }

    public boolean check() {
        return true;
    }

    public Object getPk() {
        return "0";
    }

    public void setPk(Object object) {
    }
}