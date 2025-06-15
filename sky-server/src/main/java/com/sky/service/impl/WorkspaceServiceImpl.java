package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.*;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 查询今日数据
     */
    @Override
    public BusinessDataVO getTodayBusinessData() {
        Map map = setToday();
        map.put("status", Orders.COMPLETED);

        //获取今日营业额
        Double turnover = reportMapper.turnoverStatistic(map);
        if (turnover == null) turnover = 0.0;

        //获取有效订单数
        Integer validOrderCount = orderMapper.getValidOrderCount(map);
        if (validOrderCount == null) validOrderCount = 0;

        //订单完成率
        //获取总订单数
        map.put("status", null);
        Integer orderTotal = orderMapper.countByMap(map);
        //计算订单完成率
        Double orderCompletionRate = 0.0;
        if (orderTotal != null)
            orderCompletionRate = validOrderCount * 1.0 / orderTotal;

        //计算平均客单价
        Double unitPrice = 0.0;
        if (validOrderCount != 0) unitPrice = turnover / validOrderCount;

        //新增用户数
        Integer newUsers = userMapper.getNewUserCount(map);

        return BusinessDataVO.builder()
                .turnover(turnover)
                .orderCompletionRate(orderCompletionRate)
                .validOrderCount(validOrderCount)
                .newUsers(newUsers)
                .unitPrice(unitPrice).build();
    }

    /**
     * 订单管理
     *
     * @return
     */
    @Override
    public OrderOverViewVO overviewOrders() {
        //获取今日时间
        Map map = setToday();

        //查询今日全部订单
        Integer allOrders = orderMapper.countByMap(map);

        //查询已完成的订单
        map.put("status", Orders.COMPLETED);
        Integer completedOrders = orderMapper.countByMap(map);

        //查询已取消的订单
        map.put("status", Orders.CANCELLED);
        Integer cancelledOrders = orderMapper.countByMap(map);

        //查询待派送的数量 (已接单状态)
        map.put("status", Orders.CONFIRMED);
        Integer deliveredOrders = orderMapper.countByMap(map);

        //查询待接单数量
        map.put("status", Orders.REFUND);
        Integer waitingOrders = orderMapper.countByMap(map);

        return OrderOverViewVO.builder()
                .allOrders(allOrders)
                .cancelledOrders(cancelledOrders)
                .completedOrders(completedOrders)
                .deliveredOrders(deliveredOrders)
                .waitingOrders(waitingOrders)
                .build();
    }

    /**
     * @return
     */
    @Override
    public DishOverViewVO overviewDishes() {
        //查询启售 和 停售的菜品数量
        Integer sold = dishMapper.getDishStatusCount(1);
        Integer discontinued = dishMapper.getDishStatusCount(0);

        return DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued).build();
    }

    /**
     * 套餐总览
     *
     * @return
     */
    @Override
    public SetmealOverViewVO overviewSetmeals() {
        Integer sold = setmealMapper.getSetmealStatusCount(1);
        Integer discontinued = setmealMapper.getSetmealStatusCount(0);

        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued).build();
    }

    /**
     * 获取今日0点和23点
     */
    private Map setToday() {
        //获取今日的 0点 和 23点
        LocalDateTime begin = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        return map;
    }
}
