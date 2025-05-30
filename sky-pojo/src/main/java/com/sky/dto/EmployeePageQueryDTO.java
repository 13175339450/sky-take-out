package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("分页查询时传递的员工数据模型")
public class EmployeePageQueryDTO implements Serializable {

    //员工姓名
    @ApiModelProperty("员工姓名")
    private String name;

    @ApiModelProperty("页码")
    //页码
    private int page;

    //每页显示记录数
    @ApiModelProperty("页容量")
    private int pageSize;

}
