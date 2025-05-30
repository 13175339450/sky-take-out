package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.Result;

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
}
