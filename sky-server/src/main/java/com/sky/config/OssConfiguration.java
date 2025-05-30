package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类 用于创建AliOssUtil对象
 */
@Configuration
@Slf4j
public class OssConfiguration {

    //形参使用 方法参数注入 因为AliOssProperties已经加入IOC容器
    @ConditionalOnMissingBean//没有创建容器时创建 且只创建一次，不会重复创建
    @Bean//项目启动时，自动加入ioc容器 （自动创建对象）
    public AliOssUtil createAliOssUtil(AliOssProperties aliOssProperties) {
        log.info("开始创建阿里云文件上传的配置类对象: {}", aliOssProperties);
        //利用有参构造创建对象
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}
