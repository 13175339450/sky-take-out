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
//    List<DishFlavor> queryByIds(List<Long> ids);

    /**
     * 根据ids 删除flavor信息
     * @param dishId
     * @return
     */
    int deleteByIds(List<Long> dishId);

    /**
     * 根据dishId查询口味信息
     * @param dishId
     * @return
     */
    List<DishFlavor> queryFlavorById(Long dishId);

    /**
     * 更新菜品口味
     * @param flavors
     * @return
     */
    int updateFlavor(List<DishFlavor> flavors);

    /**
     * 根据单个id删除口味信息
     * @param dishId
     * @return
     */
    int deleteById(Long dishId);

}
