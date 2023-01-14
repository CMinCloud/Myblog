package com.cm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cm.domain.dto.ArticleDto;
import com.cm.domain.entity.Article;
import com.cm.domain.dto.PageParam;
import com.cm.domain.vo.ResponseResult;



public interface ArticleService extends IService<Article> {
//    查询热门文章（根据访问量查看前十条）
    ResponseResult hotArticleList();

    ResponseResult getArticleList(PageParam articleParam);

    ResponseResult getArticleDetail(Long id);

    ResponseResult updateViewCountById(Long id);

    ResponseResult publishArticle(ArticleDto articleDto);
}
