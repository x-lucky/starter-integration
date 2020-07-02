package cn.xlucky.framework.dubbo.dto;

import lombok.Data;

/**
 * 参数
 * @author xlucky
 * @date 2020/6/30
 * @version 1.0.0
 */
@Data
public class ParamsAspectEntity {
    private String uri;
    private String methodStr;
    private long startTime;
    private long endTime;

}