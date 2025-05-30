package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel
public class CategoryPageQueryDTO implements Serializable {

    @ApiModelProperty("分类页码")
    //页码
    private int page;

    //每页记录数
    @ApiModelProperty("分类页容量")
    private int pageSize;

    //分类名称
    @ApiModelProperty("分类名称")
    private String name;

    //分类类型 1菜品分类  2套餐分类
    @ApiModelProperty("分类类型")
    private Integer type;

}
