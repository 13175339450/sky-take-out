package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("CategoryControllerUser")
@RequestMapping("user/category")
@Slf4j
@Api(tags = "用户分类相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * TODO: 根据分类类型 查询相关分类信息 (该接口的type值可有可无)
     *      当type为null时，查询所有 -> sql语句采用动态语句
     * @return
     */
    @GetMapping("list")
    @ApiOperation("条件查询")
    public Result<List<Category>> queryList(Integer type) {
        log.info("type: {}", type);
        Result result = categoryService.queryByList(type);
        return result;
    }

}
