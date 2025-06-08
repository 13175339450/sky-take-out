package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("ShopControllerAdmin")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "管理端店铺状态管理接口")
public class ShopController {
    private final String key = "SHOP_STATUS";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PutMapping("/{status}")
    @ApiOperation("设置店铺状态")
    public Result setShopStatus(@PathVariable Integer status) {
        log.info("店铺状态设置为: {}", status == 1 ? "营业中" : "打烊中");
        stringRedisTemplate.opsForValue().set(key, String.valueOf(status));
        return Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("获取店铺状态")
    public Result getShopStatus() {
        Integer status;
        String shop = stringRedisTemplate.opsForValue().get(key);
        if (shop == null) status = 0;//当店铺状态为空时，默认为打烊
        else status = Integer.valueOf(stringRedisTemplate.opsForValue().get(key));
        log.info("获取到店铺状态为: {}", status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}
