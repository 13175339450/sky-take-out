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

    /**
     * 根据id 查询套餐信息
     * @param ids
     * @return
     */
    List<DishFlavor> queryByIds(List<Long> ids);

    /**
     * 根据ids 删除flavor信息
     * @param dishId
     * @return
     */
    int deleteByIds(List<Long> dishId);
}
