package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("ShopControllerUser")
@RequestMapping("/user/shop")
@Slf4j
@Api(tags = "用户店铺状态相关接口")
public class ShopController {
    private final String key = "SHOP_STATUS";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @GetMapping("/status")
    @ApiOperation("获取店铺状态")
    public Result getShopStatus() {
        Integer status = Integer.valueOf(stringRedisTemplate.opsForValue().get(key));
        log.info("获取到店铺状态为: {}", status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}
