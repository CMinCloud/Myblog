package com.cm.service;

import com.alibaba.fastjson.JSON;
import com.cm.domain.params.PageParam;
import com.cm.domain.vo.ResponseResult;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@SpringBootTest
public class serviceTest {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private CommentService commentService;

    @Test
    void testHotArticles() {
        ResponseResult result = articleService.hotArticleList();
        System.out.println(result);
    }

    @Test
    void testCategoryList() {
//        使用自己写的sql直接完成
        ResponseResult categoryList = categoryService.getCategoryList();
        System.out.println(categoryList);
    }


    @Test
    void testCommentList(){
        ResponseResult responseResult = commentService
                .commentList(new PageParam(null, 1, 10, "1"));
        System.out.println(responseResult);
    }

    @Test
    void upload()  throws Exception{
        final String url = "http://ro5e3vz4j.hn-bkt.clouddn.com/";

        String accessKey = "GShC0skLnphH_iZ-D-RxNgk5NncuuCGbDrItq3lc";
        String accessSecretKey = "0_sm4-3ZsEBeEquIUPECnQUNA-FGmP74_6kK8WC5";
        //...生成上传凭证，然后准备上传
        String bucket = "cm-blog";

        MultipartFile file = null;
        InputStream inputStream = new FileInputStream(new File("C:\\Users\\15878\\Pictures\\Saved Pictures\\hd.jpg"));
        String fileName = "";
        String key = "2023/1/hhh";

            //构造一个带指定 Region 对象的配置类 ,选择你自己选择的存储区域：华南
            Configuration cfg = new Configuration(Region.huanan());
            //...其他参数参考类注释
            UploadManager uploadManager = new UploadManager(cfg);

            //默认不指定key的情况下，以文件内容的hash值作为文件名
            try {
//                byte[] uploadBytes = file.getBytes();
                Auth auth = Auth.create(accessKey, accessSecretKey);
                String upToken = auth.uploadToken(bucket);

//                Response response = uploadManager.put(uploadBytes, fileName, upToken);
                Response response = uploadManager.put(inputStream,key,upToken,null,null);

                //解析上传成功的结果
                DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
            } catch (Exception ex) {
                ex.printStackTrace();
            }


    }


}
