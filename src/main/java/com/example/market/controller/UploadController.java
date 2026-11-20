package com.example.market.controller;

import com.example.market.common.Result;
import com.example.market.config.UploadConfig;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * 图片上传接口。
 * 返回的是相对路径(/uploads/xxx.jpg),由前端随商品发布一起提交,存进 product.image_url。
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    // 扩展名白名单:content-type 可以被伪造,扩展名再验一次,双保险
    private static final Set<String> ALLOWED_EXT = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    private final UploadConfig uploadConfig;

    public UploadController(UploadConfig uploadConfig) {
        this.uploadConfig = uploadConfig;
    }

    /**
     * 上传商品图片。
     * Apifox 测试:POST /api/upload/image,Body 选 form-data,key 填 file,value 选图片文件。
     */
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        // 校验 1:文件非空
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }
        // 校验 2:大小限制(yml 里的 max-file-size 是第一道防线,超了根本到不了这里;
        //         这一道是为了在配置失效时也能拦住,并给出中文提示)
        if (file.getSize() > MAX_SIZE) {
            throw new RuntimeException("图片大小不能超过 5MB");
        }
        // 校验 3:扩展名白名单
        String ext = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXT.contains(ext)) {
            throw new RuntimeException("只支持 jpg / jpeg / png / gif / webp 格式");
        }

        // UUID 重命名:防止同名覆盖,也防止文件名里夹带路径(如 ../../etc/passwd)的攻击
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;

        // 保存到配置的目录(目录不存在则自动创建)
        File dir = new File(uploadConfig.getUploadDir());
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("上传目录创建失败: " + dir.getAbsolutePath());
        }
        file.transferTo(new File(dir, filename));

        // 返回相对路径而非绝对路径:以后换服务器或加 CDN,数据库里的路径不用改
        return Result.ok("/uploads/" + filename);
    }

    /** 取扩展名(统一转小写,找不到点号返回空串) */
    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.')).toLowerCase();
    }
}
