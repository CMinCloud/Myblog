package com.cm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cm.constants.SystemConstants;
import com.cm.domain.dto.ArticleDto;
import com.cm.domain.dto.ArticleListDto;
import com.cm.domain.entity.Article;
import com.cm.domain.entity.ArticleTag;
import com.cm.domain.entity.Category;
import com.cm.domain.dto.PageParam;
import com.cm.domain.entity.SystemException;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.domain.vo.ResponseResult;
import com.cm.domain.vo.ArticleDetailVo;
import com.cm.domain.vo.ArticleVo;
import com.cm.domain.vo.HotArticleVo;
import com.cm.domain.vo.PageVo;
import com.cm.mapper.ArticleMapper;
import com.cm.service.ArticleService;
import com.cm.service.ArticleTagService;
import com.cm.service.CategoryService;
import com.cm.utils.BeanCopyUtils;
import com.cm.utils.RedisCache;
import kotlin.jvm.internal.Lambda;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleTagService articleTagService;

    @Autowired
    private RedisCache redisCache;

    /*    */

    /**
     * 使用方法返回实例对象，替换成员变量注入
     *
     * @return ITbStaffService
     *//*
    public CategoryService categoryService() {
        return SpringUtils.getBean(CategoryService.class);
    }*/
    @Override
    public ResponseResult hotArticleList() {
        //查询热门文章 封装成ResponseResult返回
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //必须是正式文章
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        //按照浏览量进行排序
        queryWrapper.orderByDesc(Article::getViewCount);
        //最多只查询10条,这里使用Page查询（可以使用Limit设置）
        Page<Article> page = new Page<>(1, 10);
        page(page, queryWrapper);
        List<Article> records = page.getRecords();

//          调用工具类，赋值给vo对象
        List<HotArticleVo> hotArticleVos = BeanCopyUtils.copyBeanList(records, HotArticleVo.class);
        return ResponseResult.okResult(hotArticleVos);
    }

    @Override
    public ResponseResult getArticleList(PageParam articleParam) {
//        1.查询文章列表
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //必须是正式文章
        Long categoryId = articleParam.getId();
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL)
//                判断是否要根据categoryId查询文章列表
                .eq(Objects.nonNull(categoryId) && categoryId > 0, Article::getCategoryId, categoryId)
//                根据isTop进行降序排序
                .orderByDesc(Article::getIsTop);
//        使用分页查询
        Page<Article> articlePage = new Page<>(articleParam.getPageNum(), articleParam.getPageSize());
        page(articlePage, queryWrapper);  //设置查询操作
//        获取查询结果
        List<Article> records = articlePage.getRecords();
        long total = articlePage.getTotal();

//        2.查询所属分类名称
//        封装为pageVo
        records = records.stream().map(article -> {
            article.setCategoryName(categoryService.getById(article.getCategoryId()).getName());
            return article;
        }).collect(Collectors.toList());
/*        for (Article article : records) {
            article.setCategoryName(categoryService.getById(article.getCategoryId()).getName());
        }*/
        List<ArticleVo> articleVoList = BeanCopyUtils.copyBeanList(records, ArticleVo.class);
        PageVo pageVo = new PageVo(articleVoList, total);
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getArticleDetail(Long id) {
        Article article = getById(id);
//        先查询categoryName并封装给article
        Category category = categoryService.getById(article.getCategoryId());
        if (category != null) {
            article.setCategoryName(category.getName());
        }
        ArticleDetailVo articleDetail = new ArticleDetailVo();
        Long viewCount = redisCache.getSetSortedSetById(id);
        articleDetail.setViewCount(viewCount);
//        拷贝数据
        BeanUtils.copyProperties(article, articleDetail);
        return ResponseResult.okResult(articleDetail);
    }

    //    将修改操作在缓存中进行，查询不变还是查询数据库
    @Override
    public ResponseResult updateViewCountById(Long id) {
//        缓存更新失败暂时不报错，在控制台打印
        if (!redisCache.updateSetSortedSet(id)) {
            System.out.println("-----浏览量缓存更新失败-----");
        }
        return ResponseResult.okResult();
    }

    /**
     * 发布文章：
     * 1、保存到文章表article中
     * 2、对于文章标签，存储到article_tag中间表中
     * 3、将浏览量存入缓存
     * @param articleDto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult publishArticle(ArticleDto articleDto) {
//        检查是否输入了title，summary，content
        if (checkIsLegal(articleDto)) {
            Article article = BeanCopyUtils.copyBean(articleDto, Article.class);
            save(article);
//            再将刚刚新增的article查出来，获取id
//            todo:如果文章还是草稿，就不用将article和tag的关系插入表中
            Long articleId = article.getId();
            List<Long> tags = articleDto.getTags();
            List<ArticleTag> articleTags = tags.stream()
                    .map(tagId -> new ArticleTag(articleId, tagId)).collect(Collectors.toList());
//            新增article-tag的映射
            articleTagService.saveBatch(articleTags);
//            将浏览量存入缓存
            redisCache.setViewCount2Redis(article);
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult pageList(ArticleListDto articleListDto) {
        String title = articleListDto.getTitle();
        String summary = articleListDto.getSummary();
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(title), Article::getTitle, title);
        queryWrapper.like(StringUtils.hasText(summary), Article::getSummary, summary);
        Page<Article> articlePage = new Page<>(articleListDto.getPageNum(), articleListDto.getPageSize());
        Page<Article> page = page(articlePage, queryWrapper);
        List<ArticleVo> articleVoList = toArticleVoList(page.getRecords());
        PageVo pageVo = new PageVo(articleVoList, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getArticleDetail4Update(Long articleId) {
        LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId, articleId);
        List<ArticleTag> articleTags = articleTagService.list(queryWrapper);
        List<Long> tags = articleTags.stream().map(ArticleTag::getTagId).collect(Collectors.toList());
        Article article = getById(articleId);
        article.setTags(tags);
        return ResponseResult.okResult(article);
    }

    @Override
    public ResponseResult updateArticle(Article article) {
        try {
            updateById(article);
        } catch (Exception e) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return ResponseResult.okResult();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult deleteById(List<Long> ids) {
        boolean deleted = false;
        if (ids.size() == 1) {
//            只删除一个标签
            deleted = removeById(ids.get(0));
        }
//        批量删除
        deleted = removeByIds(ids);
//        同时删除cm_article_tag中tag和article的关联
        LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ArticleTag::getArticleId, ids);
        boolean deletedTag = articleTagService.remove(queryWrapper);
        if (deleted && deletedTag)
            return ResponseResult.okResult();
        else
            return ResponseResult.errorResult(556, "删除文章失败");
    }

    public List<ArticleVo> toArticleVoList(List<Article> articleList) {
        return BeanCopyUtils.copyBeanList(articleList, ArticleVo.class);
    }

    /**
     * 检查参数列表，是否输入了内容、简介，标题等关键字
     * @param articleDto
     * @return
     */
    private Boolean checkIsLegal(ArticleDto articleDto) {
        if (!StringUtils.hasText(articleDto.getContent())
                || !StringUtils.hasText(articleDto.getSummary())
                || !StringUtils.hasText(articleDto.getTitle())
        ) {
            throw new SystemException(AppHttpCodeEnum.KEYWORDS_NOT_NULL);
        }
        return true;
    }


}
