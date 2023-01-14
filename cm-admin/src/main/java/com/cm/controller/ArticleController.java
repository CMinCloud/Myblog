package com.cm.controller;

import com.cm.domain.dto.ArticleDto;
import com.cm.domain.vo.ResponseResult;
import com.cm.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;


    @PostMapping
    public ResponseResult publishArticle(@RequestBody ArticleDto articleDto){
        return articleService.publishArticle(articleDto);
    }
}
