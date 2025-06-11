package com.sky.service;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    /**
     * 用户提交订单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 分页查询用户历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult pageHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    OrderVO showOrderDetail(Long id);

    /**
     * 取消订单
     * @param id
     */
    void cancelOrder(Long id);

    /**
     * 用户催单
     * @param id
     */
    void reminderOrder(Long id);

    /**
     * 分页查询 + 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult pageAndSearchOrder(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 查询订单详情
     * @param id 订单id
     * @return
     */
    OrderVO queryOrderDetail(Long id);

    /**
     *
     * @return
     */
    OrderStatisticsVO getOrderStatusAmount();

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    void AdminCancelOrder(OrdersCancelDTO ordersCancelDTO);
}
