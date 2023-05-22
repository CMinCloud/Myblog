package com.cm.controller;

import com.cm.domain.dto.ArticleDto;
import com.cm.domain.dto.ArticleListDto;
import com.cm.domain.entity.Article;
import com.cm.domain.vo.ResponseResult;
import com.cm.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/content/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;


    @PostMapping
    public ResponseResult publishArticle(@RequestBody ArticleDto articleDto) {
        return articleService.publishArticle(articleDto);
    }

    @GetMapping("/list")
    public ResponseResult list(ArticleListDto articleListDto) {
        return articleService.pageList(articleListDto);
    }

    @GetMapping("/{id}")
    public ResponseResult getArticleDetailById(@PathVariable("id") Long articleId) {
        return articleService.getArticleDetail4Update(articleId);
    }

    @PutMapping
    public ResponseResult update(@RequestBody Article article){
        return articleService.updateArticle(article);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteById(@PathVariable("id") List<Long> ids){
        return articleService.deleteById(ids);
    }
}
