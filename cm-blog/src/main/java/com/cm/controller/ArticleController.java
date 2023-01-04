package com.cm.controller;

import com.cm.domain.entity.ResponseResult;
import com.cm.domain.params.ArticleListParam;
import com.cm.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article")
public class ArticleController {

    //    使用@Autowired注入依赖
    @Autowired
    private ArticleService articleService;


    @GetMapping("/hotArticleList")
    public ResponseResult hotArticleList() {
//        查询热门文章，封装成ResponseResult返回
        ResponseResult result = articleService.hotArticleList();
        return result;
    }

    @GetMapping("/articleList")
//    前后端参数名一致就不需要用@PathVariable
    public ResponseResult getArticleListInteger(Integer pageNum, Integer pageSize, Long categoryId) {
        ArticleListParam articleParam = new ArticleListParam(pageNum,pageSize,categoryId);
        return articleService.getArticleList(articleParam);
    }

    @GetMapping("/{id}")
    public ResponseResult getArticleDetail(@PathVariable("id") Long id){
       return articleService.getArticleDetail(id);
    }
}
