package com.cm.service;

import com.cm.domain.entity.ResponseResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class serviceTest {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private LoginService loginService;

    @Test
    void testHotArticles(){
        ResponseResult result = articleService.hotArticleList();
        System.out.println(result);
    }

    @Test
    void testCategoryList(){
//        使用自己写的sql直接完成
        ResponseResult categoryList = categoryService.getCategoryList();
        System.out.println(categoryList);
    }





}
