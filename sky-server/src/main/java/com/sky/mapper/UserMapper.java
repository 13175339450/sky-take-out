package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

public interface UserMapper {

    /**
     * 根据openid去user表里查询该用户的信息
     * @param openid
     * @return
     */
    User queryUserByOpenid(String openid);

    /**
     * 插入用户基本信息
     * @param user
     * @return
     */
    int insertUser(User user);

    @Select("select * from user where id = #{userId}")
    User getById(Long userId);

    /**
     * 根据时间范围去查询对应的用户数量 动态查询
     * @param map
     * @return
     */
    Integer getUserNumberByTime(Map map);

    /**
     * 获取新增用户数
     * @param map
     * @return
     */
    Integer getNewUserCount(Map map);
}
