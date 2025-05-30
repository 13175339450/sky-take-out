package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.Put;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类管理相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分类的分页查询
     */
    @GetMapping("page")
    @ApiOperation("分类的分页查询")
    public Result page(CategoryPageQueryDTO pageQueryDTO) {//param实体类传参
        log.info("分页数据: {}", pageQueryDTO);

        Result result = categoryService.queryPage(pageQueryDTO);
        return result;
    }

    /**
     * 分类菜品的启用和禁用 (普通员工也可以修改)
     */
    @PostMapping("/status/{status}")
    @ApiOperation("分类菜品的启用和禁用")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("状态和id：{} {}", status, id);
        Result result = categoryService.startOrStop(status, id);
        return result;
    }

    /**
     * 修改分类
     */
    @PutMapping
    @ApiOperation("修改分类")
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("分类信息：{}", categoryDTO);
        Result result = categoryService.updateCategory(categoryDTO);
        return result;
    }

    /**
     * 新增分类
     */
    @PostMapping
    @ApiOperation("新增分类")
    public Result insertCategory(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类信息: {}",categoryDTO);
        Result result = categoryService.insertCategory(categoryDTO);
        return result;
    }

    /**
     * 删除id删除分类
     */
    @DeleteMapping
    @ApiOperation("根据id删除分类")
    public Result deleteById(Long id){
        log.info("id值：{}", id);
        Result result = categoryService.deleteCategoryById(id);
        return result;
    }

    /**
     * 根据类型查询分类
     */
    @GetMapping("list")
    @ApiOperation("根据类型查询分类")
    public Result<Category> queryByList(Integer type){
        log.info("分类的类型：{}", type);
        Result result = categoryService.queryByList(type);
        return result;
    }
}

