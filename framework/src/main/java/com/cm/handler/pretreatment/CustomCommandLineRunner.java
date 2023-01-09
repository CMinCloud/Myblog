package com.cm.handler.pretreatment;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cm.domain.entity.Article;
import com.cm.service.ArticleService;
import com.cm.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
public class CustomCommandLineRunner implements CommandLineRunner {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisCache redisCache;


    //    项目已启动就执行进行预处理：这里将数据库中的博客浏览量存入redis
//    PS:由于redis中已存在该缓存，所以预处理修改为将redis中的缓存写入数据库
    @Override
    public void run(String... args) throws Exception {
//        将数据库中的浏览量写入缓存
       /* List<Article> articles = selectArticlePageView();
        redisCache.setSortedSet(articles);*/
        redisCache.Synchronize2Database();
//        将缓存中的浏览量同步到数据库

    }

    public List<Article> selectArticlePageView() {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("view_count");
        return articleService.list(queryWrapper);
    }
}
