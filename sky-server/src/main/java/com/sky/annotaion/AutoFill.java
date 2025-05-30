package com.sky.annotaion;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解 用于标识需要进行公共字段自动填充的方法!
 */
@Target(ElementType.METHOD)//作用在方法上
@Retention(RetentionPolicy.RUNTIME)//生命周期是在运行时有效
public @interface AutoFill {
    //数据类型为 数据库操作类型 （INSERT、UPDATE） value是一个属性 而非方法
    OperationType value();
}
