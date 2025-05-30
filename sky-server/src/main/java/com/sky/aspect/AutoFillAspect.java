package com.sky.aspect;

import com.sky.annotaion.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 定义切面类 统一拦截加入AutoFill注释的方法 并且通过反射为公共字段进行赋值
 */
@Aspect//切面注解
@Component//加入ioc容器
@Slf4j//日志输出
public class AutoFillAspect {
    /**
     * 定义切入点 指定拦截对应包下的方法 并且筛选出 加了@AutoFill注解的方法（mapper包下加注解才生效）
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotaion.AutoFill)")
    public void autoFillPointCut() {
    }

    /**
     * 前置通知 对切点拦截到的方法 执行前 执行下面的方法
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始对公共字段进行填充");
        //1.获取 连接点的方法签名 并且向下转型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        //2.获取签名(加了@AutoFill注解)对应的 方法下 的注解 autoFill为注解类的实体对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);

        //3.根据获取到的 注解对象 获取它的属性值（value()属性）
        OperationType methodType = autoFill.value();//属性值代表 对应方法的类型(INSERT/UPDATE)

        //4.准备填充所需的数据
        LocalDateTime now = LocalDateTime.now();
        Long userId = BaseContext.getCurrentId();

        //5.获取方法参数里的实体对象 （约定实体对象放在参数第一位）
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) return;//空指针判断
        Object entity = args[0];//获取实体对象（类型任意）

        //6.获取实体对象里的set方法(方法名 + 参数类型) 并且利用反射对公共字段进行赋值
        if (methodType == OperationType.INSERT) {
            try {
                //6.1获取方法
                Method createUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method updateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                Method createTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method updateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                //执行方法 (实体类 + 参数值)
                createUser.invoke(entity, userId);
                updateUser.invoke(entity, userId);
                createTime.invoke(entity, now);
                updateTime.invoke(entity, now);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (methodType == OperationType.UPDATE){
            try {
                //6.1获取方法
                Method updateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                Method updateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                //执行方法 (实体类 + 参数值)
                updateUser.invoke(entity, userId);
                updateTime.invoke(entity, now);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
