package cn.xlucky.framework.mybatis.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(
    description = "分页结果"
)
@Data
public class PagerInfo<T> {
    @ApiModelProperty(
        notes = "状态码；200：成功， 非200：失败"
    )
    private int code;
    @ApiModelProperty(
        notes = "状态信息"
    )
    private String message;
    @ApiModelProperty(
        notes = "第几页"
    )
    private int pageNum;
    @ApiModelProperty(
        notes = "每页展示几条"
    )
    private int pageSize;
    @ApiModelProperty(
        notes = "总条数"
    )
    private int total;
    @ApiModelProperty(
        notes = "总页数"
    )
    private int pages;
    @ApiModelProperty(
        notes = "分页结果数据集合"
    )
    private List<T> list;
    @ApiModelProperty(
        notes = "是否有下一页，1：有，0：否"
    )
    private int hasNext;
    @JsonIgnore
    private String totalMapperId;

    public PagerInfo() {
    }

    public PagerInfo(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}