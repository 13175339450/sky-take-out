package com.sky.mapper;

import com.sky.entity.OrderDetail;

import java.util.ArrayList;

public interface OrderDetailMapper {
    /**
     * 批量插入
     * @param orderDetails
     * @return
     */
    int insertBatch(ArrayList<OrderDetail> orderDetails);
}
