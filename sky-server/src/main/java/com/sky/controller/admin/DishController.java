package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 菜品控制层
 */
@RestController
@RequestMapping("admin/dish")
@Slf4j
@Api("菜品相关管理接口")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     */
    @PostMapping
    @ApiOperation("新增菜品和对应口味列表（甜辣温度）")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("菜品信息: {}", dishDTO);
        Result result = dishService.saveDishAndFlavor(dishDTO);
        return result;
    }

    /**
     * 分页查询
     */
    @GetMapping("page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> dishPageQuery(DishPageQueryDTO dto){
        log.info("分页查询数据：{}", dto);
        Result result = dishService.dishPageQuery(dto);
        return result;
    }
}
