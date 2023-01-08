package com.cm.utils;

import com.alibaba.fastjson.JSON;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;

import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


import java.io.InputStream;

@Component   //设置为Spring管理的Bean，从而可以读取yaml中的值
//@ConfigurationProperties(prefix = "qiniu")
public class QiniuUtils {

    public static final String url = "http://ro5e3vz4j.hn-bkt.clouddn.com/";

    @Value("${qiniu.accessKey}")
    private String accessKey;
    @Value("${qiniu.accessSecretKey}")
    private String accessSecretKey;

    public boolean upload(MultipartFile imgFile, String fileName) {

        //构造一个带指定 Region 对象的配置类 ,选择你自己选择的存储区域：华南
        Configuration cfg = new Configuration(Region.huanan());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String bucket = "cm-blog";
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        try {
//            byte[] uploadBytes = imgFile.getBytes();
//            通过传入文件来获取输入流
            InputStream inputStream = imgFile.getInputStream();
            Auth auth = Auth.create(accessKey, accessSecretKey);
//            完善填充参数
            String upToken = auth.uploadToken(bucket);
            String key = fileName;
//            Response response = uploadManager.put(uploadBytes, fileName, upToken);
            Response response = uploadManager.put(inputStream, key, upToken, null, null);
            //解析上传成功的结果
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
