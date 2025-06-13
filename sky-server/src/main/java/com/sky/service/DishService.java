package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.Result;
import com.sky.vo.DishVO;

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
    Result updateDishStatus(Integer status, Long id);

    /**
     * 根据id查询菜品 (基本信息 + 口味信息)
     * @param id
     * @return
     */
    Result queryDishFlavorById(Long id);

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    Result updateDishInfo(DishDTO dishDTO);

    /**
     * 根据categoryId查询菜品信息
     * @param categoryId
     * @return
     */
    Result queryByCategoryId(Long categoryId);

    /**
     * 根据分类categoryId在用户层查询相关信息
     * @param categoryId
     * @return
     */
    List<DishVO> queryUserByCategoryId(Long categoryId);
}
