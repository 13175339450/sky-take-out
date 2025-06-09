package com.sky.controller.user;

import com.sky.constant.RedisCacheConstant;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("SetmealControllerUser")
@RequestMapping("user/setmeal")
@Slf4j
@Api(tags = "用户套餐相关接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @GetMapping("list")
    @ApiOperation("根据分类id查询套餐信息")
    @Cacheable(cacheNames = RedisCacheConstant.SET_MEAL_CACHE_CATEGORY_ID, key = "#categoryId") //从redis里查找缓存 (categoryId 相关的)
    public Result<List<Setmeal>> queryById(Long categoryId){
        log.info("分类id: {}", categoryId);
        //起售中的套餐
        List<Setmeal> setmealList = setmealService.queryByCategoryId(categoryId);
        return Result.success(setmealList);
    }

    /**
     * 根据setmealId查询包含的菜品
     * @return
     */
    @GetMapping("/dish/{id}")
    @ApiOperation("根据setmealId查询包含的菜品")
    public Result<List<DishItemVO>> queryDishes(@PathVariable Long id){
        List<DishItemVO> dishItemVOS = setmealService.queryDishBySetmealId(id);
        return Result.success(dishItemVOS);
    }
}
