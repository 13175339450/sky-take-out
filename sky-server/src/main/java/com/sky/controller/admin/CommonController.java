package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("admin/common")
@Slf4j
@Api(tags = "公共接口管理")
public class CommonController {

    @Autowired //注入AliOss文件上传的工具类
    private AliOssUtil aliOssUtil;

    /**
     * @param file
     * @return String类型是因为要给前端返回oss中上传的文件的路径，这样子前端就能从oss中获取该文件
     */
    @PostMapping("upload")
    public Result<String> fileUpload(MultipartFile file) {
        log.info("文件上传：{}", file);
        String filePath = null;
        try {
            //1.利用UUID + 文件的格式 .jpg/.png ... 拼接一个不会重复的文件名
            //1.1获取原始文件名
            String originalFilename = file.getOriginalFilename();
            //1.2从原始文件名里提取 后缀名
            String tail = originalFilename.substring(originalFilename.lastIndexOf("."));
            //1.3构建文件名
            String fileName = UUID.randomUUID() + tail;
            //1.4构建文件请求路径
            filePath = aliOssUtil.upload(file.getBytes(), fileName);
        } catch (IOException e) {
            log.info(MessageConstant.UPLOAD_FAILED + "{}", e);//文件上传失败
        }
        return Result.success(filePath);
    }
}
