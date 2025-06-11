package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("AdminOrderController")
@RequestMapping("admin/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 分页查询 + 订单搜索
     */
    @GetMapping("conditionSearch")
    @ApiOperation("分页查询 + 订单搜索")
    public Result<PageResult> pageAndSearchOrder(OrdersPageQueryDTO ordersPageQueryDTO){
        PageResult pageResult = orderService.pageAndSearchOrder(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> queryOrderDetail(@PathVariable Long id){
        OrderVO orderVO = orderService.queryOrderDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 各个状态的订单数量统计
     */
    @GetMapping("statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<OrderStatisticsVO> anyOrderStatusAmount(){
        OrderStatisticsVO orderStatisticsVO = orderService.getOrderStatusAmount();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 取消订单
     */
    @PutMapping("cancel")
    @ApiOperation("取消订单")
    public Result cancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO){
        orderService.AdminCancelOrder(ordersCancelDTO);
        return Result.success();
    }
}
