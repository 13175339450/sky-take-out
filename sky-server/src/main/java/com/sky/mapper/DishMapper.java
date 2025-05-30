package com.sky.mapper;

import com.sky.annotaion.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;

import java.util.List;

public interface DishMapper {
    /**
     * 查询 id对应的菜品数
     * @param id
     * @return
     */
    int queryById(Long id);

    /**
     * 新增菜品
     * @param dish
     * @return
     */
    @AutoFill(OperationType.INSERT)//标记为插入数据的操作
    int saveDish(Dish dish);

    /**
     * 新增口味
     * @param flavors
     * @return
     */
    @AutoFill(OperationType.INSERT)
    int saveFlavor(List<DishFlavor> flavors);
}
