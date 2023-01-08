package com.cm.service.impl;


import com.cm.domain.entity.SystemException;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.domain.vo.ResponseResult;
import com.cm.service.UploadService;
import com.cm.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class UploadServiceImpl implements UploadService {

    @Autowired
    private QiniuUtils qiniuUtils;


    public ResponseResult uploadImg(MultipartFile img) {
//        todo：判断文件大小和类型
        String originalFilename = img.getOriginalFilename();
//        规定只能上传jpg或png格式的图片
        if (!checkImgFormat(originalFilename)) {
            throw new SystemException(AppHttpCodeEnum.IMG_TYPE_ERROR);
        }
//        上传到七牛云的oss
        String filePath = generateFilePath(originalFilename);
        boolean isUpload = qiniuUtils.upload(img, filePath);
        if (isUpload)
            return ResponseResult.okResult(QiniuUtils.url+filePath);
        else
//            七牛云过期也可能导致，重新申请一个就好
            return ResponseResult.errorResult(AppHttpCodeEnum.IMG_UPLOAD_ERROR);
    }

    public boolean checkImgFormat(String originalFilename) {
        if (!originalFilename.endsWith(".png")
                && !originalFilename.endsWith(".jpg")
                && !originalFilename.endsWith(".jpeg")) {
            return false;
        }
        return true;
    }

    public static String generateFilePath(String fileName) {
        //根据日期生成路径   2022/1/15/
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
        String datePath = sdf.format(new Date());
        //uuid作为文件名
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //后缀和文件后缀一致
        int index = fileName.lastIndexOf(".");
        // test.jpg -> .jpg
        String fileType = fileName.substring(index);
        return new StringBuilder().append(datePath).append(uuid).append(fileType).toString();
    }
}
