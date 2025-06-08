package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setMealMapper;

    @Autowired
    private SetmealDishMapper setMealDishMapper;

    /**
     * 新增套餐
     * 1.先将基本套餐信息 插入setmeal表里
     * 2.将套餐关联的菜品信息 插入 setmeal_dish表里
     *
     * @param setmealDTO
     * @return
     */
    @Transactional//加入事务
    @Override
    public Result insertSetMeal(SetmealDTO setmealDTO) {
        Setmeal setMeal = new Setmeal();
        //1.属性拷贝
        BeanUtils.copyProperties(setmealDTO, setMeal);
        //2.在setmeal表里新增套餐
        int row1 = setMealMapper.insetSetMeal(setMeal);
        if (row1 == 0) return Result.error("套餐添加失败!");

        //3.需要主键回显套餐的id
        Long setMealId = setMeal.getId();

        //4.获取套餐里包含的菜品信息
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

        //5.进行新增菜品信息 在setmeal_dish表里插入数据 一个套餐可以对应多个菜品
        if (setmealDishes != null && setmealDishes.size() > 0) {
            for (SetmealDish setMealDish : setmealDishes) {
                setMealDish.setSetmealId(setMealId);//设置套餐id值
            }
            //6.批量插入菜品信息
            int row2 = setMealDishMapper.insertSetMealDishBatch(setmealDishes);
        }

        return Result.success();
    }

    /**
     * 分页查询
     *
     * @param pageQueryDTO
     * @return
     */
    @Override
    public Result getPage(SetmealPageQueryDTO pageQueryDTO) {
        //1.设置分页数
        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());

        //2.进行分页查询 因为SetmealVo里有categoryName 可以映射为 套餐分类列
        Page<SetmealVO> page = setMealMapper.queryPage(pageQueryDTO);

        //3.数据封装
        Map map = new HashMap();
        map.put("total", page.getTotal());
        map.put("records", page.getResult());

        return Result.success(map);
    }

    /**
     * 套餐起售停售
     *
     * @param status
     * @param id
     * @return
     */
    @Override
    public Result startOrStop(Integer status, Long id) {
        int row = setMealMapper.startOrStop(status, id);
        if (row > 0) return Result.success();
        return Result.error("修改状态失败!");
    }

    /**
     * 根据id查询套餐信息 套餐id
     *
     * @param setmealId
     * @return
     */
    @Override
    public Result queryBySetmealId(Long setmealId) {
        //1.查询 基本信息 （除了List<SetmealDish>）
        SetmealVO setmealVO = setMealMapper.queryBySetmealId(setmealId);

        //2.查询 List<SetmealDish>
        List<SetmealDish> setmealDishList = setMealDishMapper.queryBySetmealId(setmealId);

        //3.赋值
        setmealVO.setSetmealDishes(setmealDishList);

        //结果封装
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐
     *
     * @param dto
     * @return
     */
    @Transactional//加入事务
    @Override
    public Result updateSetmeal(SetmealDTO dto) {
        //数据准备
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(dto, setmeal);

        //1.修改套餐的基本信息
        int row1 = setMealMapper.updateSetmealBaseInfo(setmeal);

        //2.删除原有的套餐和菜品的关联信息 (SetmealDish)
        int row2 = setMealDishMapper.deleteBySetmealId(dto.getId());

        //3.获取新的 套餐和菜品的关联信息
        List<SetmealDish> newSetmealDish = dto.getSetmealDishes();

        //4.插入新的（插入前需要进行 id设置） 套餐和菜品的关联信息
        if (newSetmealDish != null && newSetmealDish.size() > 0) {
            for (SetmealDish setmealDish : newSetmealDish) {
                setmealDish.setSetmealId(dto.getId());
            }
            int row3 = setMealDishMapper.insertSetMealDishBatch(newSetmealDish);
        }
        return Result.success();
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @Override
    @Transactional//加入事务管理
    public Result deleteBatchById(List<Long> ids) {
        //1.起售中的套餐不能删除--批量查询是否存在套餐status == 1
        List<SetmealDish> setmealDishList = setMealMapper.queryBySetmealIdAndStatus(ids);
        if (setmealDishList != null && setmealDishList.size() > 0) {
            //有任一套餐在起售 则批量删除失败
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }

        //2.删除套餐信息 setmeal表
        int row1 = setMealMapper.deleteBatchById(ids);

        //3.删除与套餐关联的菜品信息 setmeal_dish表
        int row2 = setMealDishMapper.deleteBatchBySetmealId(ids);

        return Result.success();
    }

    /**
     * 根据分类id查询套餐信息
     * @param categoryId
     * @return
     */
    @Override
    public List<Setmeal> queryByCategoryId(Long categoryId) {
        //查询起售中的套餐
        List<Setmeal> setmealList = setMealMapper.queryByCategoryId(categoryId);
        return setmealList;
    }

    /**
     * 根据setmealId查询包含的菜品
     * @param id
     * @return
     */
    @Override
    public List<DishItemVO> queryDishBySetmealId(Long id) {
        //多表联查 dish标和setmeal_dish两个标
        List<DishItemVO> setmealDishList = setMealDishMapper.queryItemBySetmealId(id);
        return setmealDishList;
    }
}
