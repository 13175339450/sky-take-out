package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotaion.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishMapper {
    /**
     * 新增菜品
     * @param dish
     * @return
     */
    @AutoFill(OperationType.INSERT)//标记为插入数据的操作
    int saveDish(Dish dish);

    /**
     * 查询 菜品id对应的菜品信息
     * @param id
     * @return
     */
    Dish queryById(Long id);

    /**
     * 新增口味
     * @param flavors
     * @return
     */
    @AutoFill(OperationType.INSERT)
    int saveFlavor(List<DishFlavor> flavors);

    /**
     * 菜品分页查询
     * @param dto
     * @return
     */
    Page<DishVO> dishPageQuery(DishPageQueryDTO dto);

    /**
     * 根据ids删除dish信息
     * @param ids
     * @return
     */
    int deleteByIds(List<Long> ids);

    /**
     * 起售或者停售商品
     * @param status
     * @param id
     * @return
     */
    int updateDishStatus(Integer status, Integer id);

    /**
     * 根据categoryId查询是否绑定了菜品
     * @param categoryId
     * @return
     */
    List<Dish> queryByCategoryId(Long categoryId);
}
