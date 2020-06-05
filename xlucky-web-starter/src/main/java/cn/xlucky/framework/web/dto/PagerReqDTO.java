package cn.xlucky.framework.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 分页入参
 * @author xlucky
 * @date 2020/6/4
 * @version 1.0.0
 */
@ApiModel(
    value = "PagerReqDTO",
    description = "分页请求对象"
)
@JsonIgnoreProperties(
    ignoreUnknown = true
)
@Data
public class PagerReqDTO extends BaseDTO {
    @ApiModelProperty(
        value = "第几页，默认1",
        name = "pageNum"
    )
    private Integer pageNum = 1;
    @ApiModelProperty(
        value = "每页展示几条，默认10",
        name = "pageSize"
    )
    private Integer pageSize = 10;

    public PagerReqDTO() {
    }

    @Override
    public String toString() {
        return "PagerReqDTO{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                '}';
    }
}