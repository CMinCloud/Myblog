package com.cm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cm.domain.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    void linkArticle2Tags(@Param("articleId") Long articleId,@Param("tagId") Long tadId);

//    List<Article> followedArticleList(List<Long> followedIds);
}
