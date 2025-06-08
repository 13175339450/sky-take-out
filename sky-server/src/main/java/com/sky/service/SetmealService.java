package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.vo.DishItemVO;

import java.util.List;

public interface SetmealService {
    /**
     * 添加套餐
     * @param setmealDTO
     * @return
     */
    Result insertSetMeal(SetmealDTO setmealDTO);

    /**
     * 分页查询
     * @param pageQueryDTO
     * @return
     */
    Result getPage(SetmealPageQueryDTO pageQueryDTO);

    /**
     * 套餐起售停售
     * @param status
     * @param id
     * @return
     */
    Result startOrStop(Integer status, Long id);

    /**
     * 根据id查询套餐信息 套餐id
     * @param id
     * @return
     */
    Result queryBySetmealId(Long id);

    /**
     * 修改套餐
     * @param dto
     * @return
     */
    Result updateSetmeal(SetmealDTO dto);

    /**
     * 根据List<Long> ids批量删除
     * @param ids
     * @return
     */
    Result deleteBatchById(List<Long> ids);

    /**
     * 根据分类id查询套餐信息
     * @param categoryId
     * @return
     */
    List<Setmeal> queryByCategoryId(Long categoryId);

    /**
     * 根据setmealId查询包含的菜品
     * @param id
     * @return
     */
    List<DishItemVO> queryDishBySetmealId(Long id);
}
