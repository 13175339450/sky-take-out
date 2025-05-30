package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotaion.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Select;

//已经在SkyApplication主类里用MapperScan扫描了 不需要为每个类单独增加扫描注解
//@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 自定义添加员工
     * @param employee
     * @return
     */
    //自定义注解 进行aop填充
    @AutoFill(OperationType.INSERT)
    int insert(Employee employee);

    /**
     * 分页查询
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO pageQueryDTO);

    /**
     * 利用entity实体类的通用更新方法
     */
    @AutoFill(OperationType.UPDATE)
    int updateByEntity(Employee employee);

    /**
     * 根据id查询员工信息
     */
    Employee queryById(Long id);
}
