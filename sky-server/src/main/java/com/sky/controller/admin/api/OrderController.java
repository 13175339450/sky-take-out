package com.sky.controller.admin.api;

import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("apiOrderController")
@RequestMapping("api/order")
@Slf4j
@Api(tags = "其他api接口")
public class OrderController {

    @Autowired
    private OrderService orderService;
    /**
     * 查看订单详情
      */
    @GetMapping("detail/{id}")
    @ApiOperation("查看订单详情")
    public Result<OrderVO> queryOrderDetail(@PathVariable Long id) {
        OrderVO orderVO = orderService.queryOrderDetail(id);
        return Result.success(orderVO);
    }
}
