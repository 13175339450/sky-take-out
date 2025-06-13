package com.sky.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private SetmealMapper setMealMapper;

    @Autowired
    private RedisTemplate redisTemplate;

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
        //1.3新增操作 (使用AutoFill填充)
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
        List<SetmealDish> mealDishList = setMealMapper.queryByIds(ids);
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

        //2.更新数据 基本菜品信息数据 在dish菜品表里的数据 在这里使用AutoFill填充时间
        int row1 = dishMapper.updateDishBaseInfo(dish);

        //3.删除旧的口味数据 （在dish_flavor口味表里的数据）
        int row2 = dishFlavorMapper.deleteById(dishDTO.getId());

        //4.获取新的口味信息 （前端提交的修改后的口味数据）
        List<DishFlavor> flavors = dishDTO.getFlavors();

        //5.将口味信息更新
        if(flavors != null && flavors.size() > 0){
            //1.更新dishId
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishDTO.getId());
            }
            //插入新的口味信息 （批量添加）
            //批处理不能使用AutoFill 因为批处理返回的是List<entity> 但只能识别单个entity对象
            int row3 = dishFlavorMapper.saveBatch(flavors);
        }

        if(row1 > 0) return Result.success();
        return Result.error("更新信息失败!");
    }

    /**
     * 根据categoryId查询菜品（为套餐管理的添加套餐服务）
     * @param categoryId
     * @return
     */
    @Override
    public Result queryByCategoryId(Long categoryId) {
        List<Dish> dishList = dishMapper.queryByCategoryId(categoryId);
        return Result.success(dishList);
    }

    /**
     * 在用户端：根据分类categoryId查询相关信息
     * @param categoryId
     * @return
     */
    @Override
    public List<DishVO> queryUserByCategoryId(Long categoryId) {
        //1.先构造redis里的key
        String key = "dish_" + categoryId;

        //2.1先从redis里查询该key是否有值 反序列化
        List<DishVO> dishVOS = (List<DishVO>) redisTemplate.opsForValue().get(key);

        //有值直接返回
        if(dishVOS != null) return dishVOS;

        //无值先获取，再序列化
        //1.先根据分类id查 dish表 获取基本信息 （多组数据） 要求status=1的起售中的
        List<Dish> dishList = dishMapper.queryByCategoryId(categoryId);
        System.out.println("yyyyyyy");

        //2.根据分类categoryId获取categoryName
        String categoryName = dishMapper.queryCategoryName(categoryId);

        System.out.println("xxxxxx");
        //3.为每组数据查询相关口味信息 并且封装
        dishVOS = new ArrayList<>();
        for (Dish dish : dishList) {
            //3.1准备对应的实体类
            DishVO dishVO = new DishVO();
            //3.2赋值
            BeanUtils.copyProperties(dish, dishVO);
            //3.3封装categoryName
            dishVO.setCategoryName(categoryName);

            //3.4.查询对应的菜品口味 根据dishId
            List<DishFlavor> dishFlavorList = dishFlavorMapper.queryFlavorById(dish.getId());
            //3.5数据封装
            dishVO.setFlavors(dishFlavorList);

            //3.6结果存储
            dishVOS.add(dishVO);
        }
        //序列化
        redisTemplate.opsForValue().set(key, dishVOS);

        //4.结果返回
        return dishVOS;
    }
}
