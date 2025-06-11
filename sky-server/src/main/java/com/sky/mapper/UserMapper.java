package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Select;

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
}
