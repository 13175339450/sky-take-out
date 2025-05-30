package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.Result;

import java.util.List;

public interface DishService {
    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    Result saveDishAndFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dto
     * @return
     */
    Result dishPageQuery(DishPageQueryDTO dto);

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    Result deleteBatch(List<Long> ids);

    /**
     * 起售、停售商品
     * @param status
     * @param id
     * @return
     */
    Result updateDishStatus(Integer status, Integer id);
}
