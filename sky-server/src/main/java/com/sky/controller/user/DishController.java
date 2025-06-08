package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("DishControllerUser")
@RequestMapping("user/dish")
@Slf4j
@Api(tags = "小程序菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;
    @GetMapping("list")
    @ApiOperation("根据分类id查询菜品信息")
    public Result<List<DishVO>> queryByCategoryId(Long categoryId){
        log.info("分类id: {}", categoryId);

        //查询起售中的菜品
        Result result = dishService.queryUserByCategoryId(categoryId);

        return result;
    }
}
