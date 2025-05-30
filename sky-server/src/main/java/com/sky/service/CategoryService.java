package com.sky.service;

import com.sky.annotaion.AutoFill;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.enumeration.OperationType;
import com.sky.result.Result;

public interface CategoryService {
    /**
     * 分页查询
     * @param pageQueryDTO
     * @return
     */
    Result queryPage(CategoryPageQueryDTO pageQueryDTO);

    /**
     * 分类菜品的启用和禁用
     * @param status
     * @param id
     * @return
     */
    Result startOrStop(Integer status, Long id);

    /**
     * 修改分类
     * @param categoryDTO
     * @return
     */
    Result updateCategory(CategoryDTO categoryDTO);

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    Result insertCategory(CategoryDTO categoryDTO);

    /**
     * 根据id删除分类
     * @return
     */
    Result deleteCategoryById(Long id);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    Result queryByList(Integer type);
}
