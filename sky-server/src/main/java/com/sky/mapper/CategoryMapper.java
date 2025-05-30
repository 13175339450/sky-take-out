package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotaion.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;

import java.util.List;

//已经在SkyApplication主类里用MapperScan扫描了 不需要为每个类单独增加扫描注解
//@Mapper
public interface CategoryMapper {
    /**
     * 分类菜品的分页查询
     * @param pageQueryDTO
     * @return
     */
    Page<Category> queryPage(CategoryPageQueryDTO pageQueryDTO);

    /**
     * 分类菜品的启用或禁用
     *
     * @param build
     * @return
     */
    @AutoFill(OperationType.UPDATE)
    int updateCategoryInfo(Category build);

    /**
     * 新增分类
     * @param category
     * @return
     */
    @AutoFill(OperationType.INSERT)
    int insertCategory(Category category);

    /**
     * 根据id删除菜品
     * @param id
     * @return
     */
    int deleteCategoryById(Long id);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    List<Category> queryByList(Integer type);
}
