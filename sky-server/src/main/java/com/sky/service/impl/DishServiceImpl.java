package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.Result;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service//加入ioc容器
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品和对应口味列表（甜辣温度）
     *
     * @param dishDTO
     * @return
     */
    @Transactional//加入事务注解 插入成功必须菜品和口味全部插入成功！
    @Override
    public Result saveDishAndFlavor(DishDTO dishDTO) {
        //1.1新增菜品
        Dish dish = new Dish();
        //1.2属性拷贝
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setStatus(1);//设置状态为1
        //1.3新增操作
        int row1 = dishMapper.saveDish(dish);

        //2.1设置回显的菜品id
        Long dishId = dish.getId();//获取回显后的dishId值
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishId);
            }
            //2.2进行批量insert操作 ()
            //菜品口味可以不添加
            int row2 = dishFlavorMapper.saveBatch(flavors);
        }
        if (row1 > 0) return Result.success(MessageConstant.DISH_INSERT_SUCCESS);
        return Result.error(MessageConstant.DISH_INSERT_FAIL);
    }
}
