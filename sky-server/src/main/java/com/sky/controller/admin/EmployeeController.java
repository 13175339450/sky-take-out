package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 添加员工
     */
    @PostMapping
    @ApiOperation("添加员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        Result result = employeeService.save(employeeDTO);
        return result;
    }

    /**
     * 分页查询员工信息
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO page){//param实体传参 默认不加注解
        log.info("接收到的参数为:{}", page);

        //调用业务层
        PageResult pageResult = employeeService.pageQuery(page);

        return Result.success(pageResult);
    }

    /**
     * 启用或禁用员工账号
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用或禁用员工")
    public Result startOrStop(@PathVariable Integer status, Long id){//路径传参 + query传参
        Result result = employeeService.startOrStop(status, id);
        return result;
    }

    /**
     * 根据id查询员工信息
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询员工信息")
    public Result<Employee> queryById(@PathVariable Long id){
        log.info("id值为：{}", id);
        Result result = employeeService.queryById(id);
        return result;
    }

    /**
     * 编辑员工信息
     */
    @PutMapping
    @ApiOperation("编辑员工信息")
    public Result updateEmployeeInfo(@RequestBody EmployeeDTO employeeDTO){//接收JSON格式
        log.info("员工信息: {}", employeeDTO);
        Result result = employeeService.updateEmployeeInfo(employeeDTO);
        return result;
    }
}
