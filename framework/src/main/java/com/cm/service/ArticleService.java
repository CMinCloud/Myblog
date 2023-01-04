package com.cm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cm.domain.entity.Article;
import com.cm.domain.entity.ResponseResult;
import com.cm.domain.params.ArticleListParam;


public interface ArticleService extends IService<Article> {
//    查询热门文章（根据访问量查看前十条）
    ResponseResult hotArticleList();

    ResponseResult getArticleList(ArticleListParam articleParam);

    ResponseResult getArticleDetail(Long id);
}
