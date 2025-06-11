package com.sky.mapper;

import com.sky.entity.OrderDetail;

import java.util.ArrayList;
import java.util.List;

public interface OrderDetailMapper {
    /**
     * 批量插入
     * @param orderDetails
     * @return
     */
    int insertBatch(ArrayList<OrderDetail> orderDetails);

    /**
     * 查询每个订单对应的明细
     * @param id
     * @return
     */
    List<OrderDetail> queryDetailByOrderId(Long id);
}
