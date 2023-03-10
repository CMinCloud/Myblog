package com.cm.controller;

import com.cm.common.aop.LogAnnotation;
import com.cm.domain.dto.PageParam;
import com.cm.domain.vo.ResponseResult;
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
    @LogAnnotation(module = "文章模块",operation = "文章列表")
//    前后端参数名一致就不需要用@PathVariable
    public ResponseResult getArticleList(Integer pageNum, Integer pageSize, Long categoryId) {
        PageParam articleParam = new PageParam(categoryId,pageNum,pageSize);
        return articleService.getArticleList(articleParam);
    }

    @GetMapping("/{id}")
    public ResponseResult getArticleDetail(@PathVariable("id") Long id){
       return articleService.getArticleDetail(id);
    }

    @PutMapping("updateViewCount/{id}")
    public ResponseResult updateViewCountById(@PathVariable("id") Long id){
        return articleService.updateViewCountById(id);
    }
}
