package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetMealMapper setMealMapper;

    /**
     * 分类的分页查询
     */
    @Override
    public Result queryPage(CategoryPageQueryDTO pageQueryDTO) {
        //1.设置分页数据
        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());

        //2.进行分页查询 数据格式要标准
        Page<Category> page = categoryMapper.queryPage(pageQueryDTO);

        //3.获取需要封装的数据
        long total = page.getTotal();
        List<Category> records = page.getResult();

        //4.数据封装 使用Map集合
        Map map = new HashMap();
        map.put("total", total);
        map.put("records", records);

        //5.返回最终封装结果
        return Result.success(map);
    }

    /**
     * 分类菜品的启用和禁用
     */
    @Override
    public Result startOrStop(Integer status, Long id) {
        //创建实体类对象
        Category.CategoryBuilder categoryBuilder = Category.builder()
                .status(status).id(id);
        int rows = categoryMapper.updateCategoryInfo(categoryBuilder.build());
        if (rows > 0) return Result.success();
        return Result.error("状态修改失败!");
    }

    /**
     * 修改分类
     */
    @Override
    public Result updateCategory(CategoryDTO categoryDTO) {
        Category category = new Category();

        //1.类型转换
        BeanUtils.copyProperties(categoryDTO, category);

        //2.修改 相关时间参数 ThreadLocal
//        category.setUpdateTime(LocalDateTime.now());
//        category.setUpdateUser(BaseContext.getCurrentId());

        //3.提交修改
        int rows = categoryMapper.updateCategoryInfo(category);

        if(rows > 0) return Result.success();
        return Result.error("修改分类失败!");
    }

    /**
     * 新增 分类 和 套餐
     */
    @Override
    public Result insertCategory(CategoryDTO categoryDTO) {
        //1.创建实体类
        Category category = new Category();

        //2.类型转换
        BeanUtils.copyProperties(categoryDTO, category);

        //3.设置必要参数
        category.setStatus(1);
//        category.setCreateUser(BaseContext.getCurrentId());
//        category.setUpdateUser(BaseContext.getCurrentId());
//        category.setCreateTime(LocalDateTime.now());
//        category.setUpdateTime(LocalDateTime.now());

        //4.进行插入
        int rows = categoryMapper.insertCategory(category);

        if (rows > 0) return Result.success();
        return Result.error("添加分类失败!");
    }

    /**
     * 根据id删除分类
     */
    @Override
    public Result deleteCategoryById(Long id) {//此id是categoryId
        //1.先判断该分类ID是否 绑定了 菜品(categoryId)
        List<Dish> dish = dishMapper.queryByCategoryId(id);
        if(dish != null){
            //绑定了菜品 抛出异常
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        //2.再判断该分类是否 绑定了 套餐
        int setMealCounts = setMealMapper.queryById(id);
        if(setMealCounts > 0) {
            //绑定了套餐 抛出异常
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //3.可以删除 执行删除操作
        int rows = categoryMapper.deleteCategoryById(id);
        if(rows > 0) return Result.success();
        return Result.error("删除失败!");
    }

    /**
     * 根据类型查询分类
     */
    @Override
    public Result queryByList(Integer type) {
        //查询
        List<Category> category = categoryMapper.queryByList(type);

        //封装
        return Result.success(category);
    }
}
