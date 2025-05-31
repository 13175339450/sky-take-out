package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐管理相关接口")
public class SetmealController {

    @Autowired
    private SetmealService setMealService;

    /**
     * 新增套餐
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result insertSetMeal(@RequestBody SetmealDTO setmealDTO) {
        Result result = setMealService.insertSetMeal(setmealDTO);
        return result;
    }

    /**
     * 分页查询套餐信息
     */
    @GetMapping("page")
    @ApiOperation("分页查询套餐信息")
    public Result<PageResult> page(SetmealPageQueryDTO pageQueryDTO) {
        log.info("分页数据: {}", pageQueryDTO);
        Result result = setMealService.getPage(pageQueryDTO);
        return result;
    }

    /**
     * 套餐起售停售
     */
    @PostMapping("status/{status}")
    @ApiOperation("套餐起售停售")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        Result result = setMealService.startOrStop(status, id);
        return result;
    }

    /**
     * 根据id查询套餐信息 用于修改套餐时的 信息回显
     */
    @GetMapping("{id}")
    @ApiOperation("根据id查询套餐信息 用于修改套餐时的 信息回显")
    public Result<SetmealVO> queryById(@PathVariable Long id) {
        Result result = setMealService.queryBySetmealId(id);
        return result;
    }

    /**
     * 修改套餐
     */
    @PutMapping
    @ApiOperation("修改套餐")
    public Result updateSetmeal(@RequestBody SetmealDTO dto){
        Result result = setMealService.updateSetmeal(dto);
        return result;
    }

    /**
     * 批量删除套餐
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    public Result deleteByIds(@RequestParam List<Long> ids){
        Result result = setMealService.deleteBatchById(ids);
        return result;
    }
}
