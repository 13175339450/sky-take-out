package com.sky.mapper;

import com.sky.entity.SetmealDish;

import java.util.List;

public interface SetmealDishMapper {
    /**
     * 批量添加套餐绑定的菜品
     * @param setmealDishes
     * @return
     */
    int insertSetMealDishBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据菜品id查询 菜品相关信息
     * @param setmealId
     * @return
     */
    List<SetmealDish> queryBySetmealId(Long setmealId);

    /**
     * 根据套餐setmeal_id 删除相关联的菜品信息
     * @param id
     * @return
     */
    int deleteBySetmealId(Long id);

    /**
     * 根据套餐setmeal_id 批量删除相关联的菜品信息
     * @param ids
     * @return
     */
    int deleteBatchBySetmealId(List<Long> ids);

}
