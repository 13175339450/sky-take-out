package com.sky.mapper;

import com.sky.entity.Orders;

public interface OrderMapper {
    /**
     * 主键回显
     * @param orders
     * @return
     */
    int insert(Orders orders);

}
