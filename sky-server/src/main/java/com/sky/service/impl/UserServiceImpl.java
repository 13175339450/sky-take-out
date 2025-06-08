package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.constant.WeChatConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatProperties weChatProperties;

    /**
     * 用户登录接口
     *
     * @param userLoginDTO
     * @return
     */
    @Override
    public User weChatLogin(UserLoginDTO userLoginDTO) {

        //1.根据通用函数获取openid
        String openid = getOpenid(userLoginDTO.getCode());

        //2.判断openid是否为空 为空表示没有该用户 登录失败
        if (null == openid) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //3.判断当前用户是否为新用户 从user表里查找是否存在该用户 根据openid(每个用户都唯一且不变)
        User user = userMapper.queryUserByOpenid(openid);

        //4.如果是新用户 自动完成注册
        if (user == null) {
            //5.1封装简单的用户信息
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            //5.2插入新用户数据
            int row = userMapper.insertUser(user);
        }

        //返回用户
        return user;
    }

    public String getOpenid(String code) {
        //1.根据HttpClientGet获取openId
        //1.1 创建map 并且存储对应的用户数据
        Map map = new HashMap();
        map.put(WeChatConstant.APP_ID, weChatProperties.getAppid());
        map.put(WeChatConstant.SECRET, weChatProperties.getSecret());
        map.put(WeChatConstant.JS_CODE, code);
        map.put(WeChatConstant.GRANT_TYPE, WeChatConstant.GRANT_TYPE_VALUE);
        //1.2根据用户数据获取响应的json
        String json = HttpClientUtil.doGet(WeChatConstant.LOGIN_URL, map);

        //2.1将JSON转换
        JSONObject jsonObject = JSON.parseObject(json);

        //2.2获取openid
        return jsonObject.getString("openid");
    }
}
