package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetMealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service//加入ioc容器
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetMealMapper setMealMapper;


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

    /**
     * 菜品分页查询
     *
     * @param dto
     * @return
     */
    @Override
    public Result dishPageQuery(DishPageQueryDTO dto) {
        //1.设置分页查询的分页数据
        PageHelper.startPage(dto.getPage(), dto.getPageSize());

        //2.分页查询 格式要正确
        Page<DishVO> page = dishMapper.dishPageQuery(dto);

        //3.结果封装
        Map map = new HashMap();
        map.put("total", page.getTotal());
        map.put("records", page.getResult());

        return Result.success(map);
    }

    /**
     * 批量删除菜品
     *
     * @param ids
     * @return
     */
    @Transactional//设置事务
    @Override
    public Result deleteBatch(List<Long> ids) {
        //1.判断当前菜品是否能够删除--是否正在起售？？ (只要有一个菜品在起售，就批量删除失败)
        for (Long id : ids) {
            Dish dish = dishMapper.queryById(id);
            if (dish != null && dish.getStatus() > 0) {
                //抛出自定义的异常 起售中的套餐不能删除!
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        //2.判断当前菜品是否能够删除--是否关联了套餐？？(只要一个菜品关联了套餐，就批量删除失败)
        List<SetMealDish> mealDishList = setMealMapper.queryByIds(ids);
        if (mealDishList != null && mealDishList.size() > 0) {
            //抛出自定义的异常 起售中的套餐不能删除!
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        //3.删除菜品表中的菜品数据
        int dishRows = dishMapper.deleteByIds(ids);

        //4.删除该菜品关联的口味数据 （可能没有关联）
        int flavorRows = dishFlavorMapper.deleteByIds(ids);

        if (dishRows > 0) {
            return Result.success();
        }
        return Result.error("异常原因导致删除失败!");
    }

    /**
     * 起售、停售商品
     * @param status
     * @param id
     * @return
     */
    @Override
    public Result updateDishStatus(Integer status, Long id) {
        int row = dishMapper.updateDishStatus(status, id);
        if (row > 0) return Result.success();
        return Result.error("商品状态修改失败");
    }

    /**
     * 根据id查询菜品 (基本信息 + 口味信息)
     *
     * @param id
     * @return
     */
    @Override
    public Result queryDishFlavorById(Long id) {
        DishVO dishVO = new DishVO();
        //1.根据id查询基本菜品信息
        Dish dish = dishMapper.queryById(id);

        //2.属性拷贝
        BeanUtils.copyProperties(dish, dishVO);

        //3.根据dishId查询口味信息
        List<DishFlavor> flavors = dishFlavorMapper.queryFlavorById(id);

        //4.赋值
        dishVO.setFlavors(flavors);

        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     *
     * @param dishDTO
     * @return
     */
    @Transactional//加入事务管理
    @Override
    public Result updateDishInfo(DishDTO dishDTO) {
        //1.将传入的修改后的信息 封装到dish里
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //2.更新数据 基本菜品信息数据
        int row1 = dishMapper.updateDishBaseInfo(dish);

        //3.删除旧的口味数据
        int row2 = dishFlavorMapper.deleteById(dishDTO.getId());

        //4.获取新的口味信息
        List<DishFlavor> flavors = dishDTO.getFlavors();

        //5.将口味信息更新
        if(flavors != null && flavors.size() > 0){
            //1.更新dishId
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishDTO.getId());
            }
            //插入新的口味信息
            int row3 = dishFlavorMapper.saveBatch(flavors);
        }

        if(row1 > 0) return Result.success();
        return Result.error("更新信息失败!");
    }
}
