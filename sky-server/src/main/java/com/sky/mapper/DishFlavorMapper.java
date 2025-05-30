package com.sky.mapper;

import com.sky.entity.DishFlavor;

import java.util.List;

public interface DishFlavorMapper {
    /**
     * 批量增加菜品口味
     * @param flavors
     * @return
     */
    int saveBatch(List<DishFlavor> flavors);
}
