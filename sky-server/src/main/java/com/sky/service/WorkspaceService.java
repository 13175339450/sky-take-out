package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

public interface WorkspaceService {
    /**
     * 查询今日数据
     * @return
     */
    BusinessDataVO getTodayBusinessData();

    /**
     * 订单管理
     * @return
     */
    OrderOverViewVO overviewOrders();

    /**
     * 菜品总览
     * @return
     */
    DishOverViewVO overviewDishes();

    /**
     * 套餐总览
     * @return
     */
    SetmealOverViewVO overviewSetmeals();

}
