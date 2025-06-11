package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface OrderMapper {
    /**
     * 主键回显
     * @param orders
     * @return
     */
    int insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据订单id查询订单表的订单信息
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders queryById(Long id);

    /**
     * 分页查询
     *
     * @param userId
     * @param status
     * @return
     */
    Page<Orders> pageHistoryOrders(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 取消订单
     * @param id
     * @return
     */
    @Update("update orders set status = 6 where id = #{id}")
    int cancelOrder(Long id);

    /**
     * 分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageUserOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * TODO： sql优化
     * @return
     */
    OrderStatisticsVO getOrderStatusAmount();

    /**
     * 商家取消订单 并且添加理由
     * @param ordersCancelDTO
     */
    void AdminCancelReson(OrdersCancelDTO ordersCancelDTO);
}
