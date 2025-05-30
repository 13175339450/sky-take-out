package com.sky.mapper;

import com.sky.entity.SetMealDish;

import java.util.List;

public interface SetMealMapper {
    /**
     * 查询 id 绑定的套参数
     * @param id
     * @return
     */
    int queryById(Long id);

    /**
     * 查询当前菜品是否关联了套餐
     * @param dishIds
     * @return
     */
    List<SetMealDish> queryByIds(List<Long> dishIds);
}
