package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.beans.beancontext.BeanContext;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据 (账号被锁定 也能查询到数据 没有使用逻辑删除)
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //1.将明文密码改成密文
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2.用数据库里的密文密码比对 输入的明文转密文的密码
        if (!employee.getPassword().equals(password)) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        //处理账号被锁定
        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 添加员工
     * @param employeeDTO
     * @return
     */
    @Override
    public Result save(EmployeeDTO employeeDTO) {
        /* 参数传递的是DTO对象 而与sql交接的是entity对象 需要进行转化*/
        Employee employee = new Employee();

        //1.对象转化 DTO -> ENTITY 属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);

        //2.剩余参数添加
        //2.1 添加员工状态 可用
        employee.setStatus(StatusConstant.ENABLE);

        //2.2添加员工默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        //2.3添加员工创建时间和修改时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //***** 根据ThreadLocal 获取在拦截器里存放进去的empId值 *****
//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());

        //3.数据插入 Mybatis自定义插入
        int rows = employeeMapper.insert(employee);

        if(rows > 0){ return Result.success();}
        return Result.error("添加员工失败!");//在添加提交前 已经有人抢先添加了 导致不能重复添加！
    }


    /**
     * 分页查询
     * @param pageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO pageQueryDTO) {
        //1.设置分页查询的 偏移量 和 页容量
        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());

        //2.根据持久层查询分页信息
        Page<Employee> page = employeeMapper.pageQuery(pageQueryDTO);

        //3.进行结果封装
        long total = page.getTotal();
        List<Employee> records = page.getResult();

        return new PageResult(total, records);//调用有参构造器
    }

    /**
     * TODO:启用或禁用员工账号 （功能修改为只有管理员才能进行该操作）
     */
    @Override
    public Result startOrStop(Integer status, Long id) {
        //1.用实体类封装参数 builder构造
        Employee.EmployeeBuilder employee = Employee.builder()
                .status(status).id(id);

        //2.自定义通用的update方法 利用实体类更新
        int rows = employeeMapper.updateByEntity(employee.build());
        if (rows > 0) return Result.success();
        return Result.error("员工不存在!");//在修改员工信息前 已经有人删除该员工了！
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @Override
    public Result queryById(Long id) {
        Employee employee = employeeMapper.queryById(id);
        if (null != employee) return Result.success(employee);
        return Result.error("员工id不存在!");
    }

    /**
     * 编辑员工信息
     *
     * @param employeeDTO
     * @return
     */
    @Override
    public Result updateEmployeeInfo(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        //1.进行类型转换 自定义的update方法 需要传入employee类
        BeanUtils.copyProperties(employeeDTO, employee);

        //2.设置更新时间 和 修改人(根据ThreadLocal获取 拦截器里已经设置了)
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(BaseContext.getCurrentId());

        //3.传入转换后的employee类型
        int rows = employeeMapper.updateByEntity(employee);

        if(rows > 0) return Result.success();
        return Result.error("更新失败!");
    }

}
