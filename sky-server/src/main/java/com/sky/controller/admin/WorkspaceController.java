package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/workspace")
@Slf4j
@Api(tags = "工作台相关接口")
public class WorkspaceController {
    @Autowired
    private WorkspaceService workspaceService;

    @GetMapping("businessData")
    @ApiOperation("查询今日运行数据")
    public Result<BusinessDataVO> getTodayBusinessData(){
        BusinessDataVO vo = workspaceService.getTodayBusinessData();
        return Result.success(vo);
    }

    @GetMapping("overviewOrders")
    @ApiOperation("订单管理")
    public Result<OrderOverViewVO> overviewOrders() {
        OrderOverViewVO vo = workspaceService.overviewOrders();
        return Result.success(vo);
    }

    @GetMapping("overviewDishes")
    @ApiOperation("菜品总览")
    public Result<DishOverViewVO> overviewDishes(){
        DishOverViewVO vo = workspaceService.overviewDishes();
        return Result.success(vo);
    }

    @GetMapping("overviewSetmeals")
    @ApiOperation("套餐总览")
    public Result<SetmealOverViewVO> overviewSetmeals(){
        SetmealOverViewVO vo = workspaceService.overviewSetmeals();
        return Result.success(vo);
    }
}
