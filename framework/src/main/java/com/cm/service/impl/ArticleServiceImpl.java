package com.cm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cm.constants.SystemConstants;
import com.cm.domain.dto.ArticleDto;
import com.cm.domain.dto.ArticleListDto;
import com.cm.domain.entity.*;
import com.cm.domain.dto.PageParam;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.domain.vo.ResponseResult;
import com.cm.domain.vo.ArticleDetailVo;
import com.cm.domain.vo.ArticleVo;
import com.cm.domain.vo.HotArticleVo;
import com.cm.domain.vo.PageVo;
import com.cm.mapper.ArticleMapper;
import com.cm.service.*;
import com.cm.utils.BeanCopyUtils;
import com.cm.utils.RedisCache;
import com.cm.utils.SecurityUtils;
import kotlin.jvm.internal.Lambda;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xin.altitude.cms.common.util.SpringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private CategoryService categoryService;

//    使用方法注入替代循环依赖
    public CategoryService getCategoryService(){
        return SpringUtils.getBean(CategoryService.class);  //如果有多个实例bean则通过名称获取
    }

    @Autowired
    private ArticleTagService articleTagService;

    @Autowired
    private FollowService followService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private UserService userService;

    /*    */

    /**
     * 使用方法返回实例对象，替换成员变量注入
     *
     * @return ITbStaffService
     /*
    */
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
        page(articlePage, queryWrapper);
//        获取查询结果
        List<Article> records = articlePage.getRecords();
        long total = articlePage.getTotal();
//        拷贝为vo对象
        List<ArticleVo> articleVoList = BeanCopyUtils.copyBeanList(records, ArticleVo.class);
        articleVoList = articleVoList.stream().map(vo -> {
//            封装分类名称
            vo.setCategoryName(categoryService.getById(vo.getCategoryId()).getName());
//            封装作者
            vo.setAuthor(userService.getById(vo.getCreateBy()).getNickName());
//            封装用户名称
            return vo;
        }).collect(Collectors.toList());
        //        封装为pageVo
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
        articleDetail.setAuthor(userService.getById(article.getCreateBy()).getNickName());
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
     *
     * @param articleDto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult publishArticle(ArticleDto articleDto) {
//        检查是否输入了title，summary，content
        if (!checkIsLegal(articleDto)) {
            return ResponseResult.errorResult(560, "文章发表失败");
        }
        Article article = BeanCopyUtils.copyBean(articleDto, Article.class);
//            初始化基本数据
        article.setDelFlag(0);
        article.setViewCount(0L);
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
//        将文章推送到粉丝处
        Long userId = SecurityUtils.getLoginUser().getUser().getId();
//        1.获取所有粉丝id
        Set<String> fanIds = redisCache.getCacheSet(RedisCache.BlogFansKey + userId);
        if (Objects.isNull(fanIds)) {
//            缓存查询失败，从数据库中查询
            QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("follow_user_id", userId);
            List<Follow> followList = followService.list(queryWrapper);
            fanIds = followList.stream().map(follow -> follow.getUserId().toString()).collect(Collectors.toSet());
        }
//        2.推送
        fanIds.forEach(id -> {
            System.out.println(id.getClass());
            redisCache.addFollowArticle2Redis(Long.valueOf(id), articleId);
        });
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
        if (deleted && deletedTag) {
            return ResponseResult.okResult();
        } else {
            return ResponseResult.errorResult(556, "删除文章失败");
        }
    }

    /**
     * 获取当前用户关注的文章列表
     * 使用feed流先获取redis中文章id，再根据文章id查询数据库
     * 注意：feed流的分页不能采取原始方式，需要记录上次查找的最后一条数据
     * 下次查询从最后一条数据后开始
     * @param pageParam
     * @return
     */
    @Override
    public ResponseResult followedArticleList(PageParam pageParam) {
//        --获取当前用户id
        Long id;
        try {
            id = SecurityUtils.getLoginUser().getUser().getId();
        } catch (Exception e) {
            throw new SystemException(AppHttpCodeEnum.NEED_LOGIN);
        }

//        1：直接从redis中的feed缓存查询
        Set articleIds = null;
        try{
           articleIds = redisCache.getFollowArticle4Redis(id);
           if(articleIds.size() <= 0){
               throw new SystemException(AppHttpCodeEnum.NO_FOLLOW);
           }
        }catch (Exception e){
            System.out.println(e.getMessage());  // 打印错误日志
        }
        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        1.1：根据缓存得到的articleId查询
        if(Objects.nonNull(articleIds)){
            Long categoryId = pageParam.getId();
            lambdaQueryWrapper.eq(Article::getStatus,SystemConstants.ARTICLE_STATUS_NORMAL)
                    .eq(categoryId>0,Article::getCategoryId,categoryId)
                    .in(Article::getId,articleIds)
                    .orderByDesc(Article::getCreateTime);
        }else {
 //        2：缓存中没有值，则从数据库中查询所有自己关注的博主的文章
            QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", id);
            List<Follow> followList = followService.list(queryWrapper);
//        2.1：关注者列表
            List<Long> followedList = followList.stream().map(follow -> follow.getFollowUserId()).collect(Collectors.toList());
            if (followedList.size() <= 0) {
                throw new SystemException(AppHttpCodeEnum.NO_FOLLOW);
            }
//        2.2、在根据关注对象followedId来查找文章
            Long categoryId = pageParam.getId();
            lambdaQueryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL)
                    .eq(categoryId > 0, Article::getCategoryId, categoryId)
                    .in(followedList.size() > 0, Article::getCreateBy, followedList)
                    .orderByDesc(Article::getCreateTime);   //关注者列表
        }

//        3. 分页查询
        Page<Article> articlePage = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());
        page(articlePage, lambdaQueryWrapper);
        List<Article> records = articlePage.getRecords();
        long total = articlePage.getTotal();
//        3.1 拷贝未vo对象
        List<ArticleVo> articleVoList = BeanCopyUtils.copyBeanList(records, ArticleVo.class);
        articleVoList = articleVoList.stream().map(vo -> {
//            封装分类名称
            vo.setCategoryName(categoryService.getById(vo.getCategoryId()).getName());
//            封装作者
            vo.setAuthor(userService.getById(vo.getCreateBy()).getNickName());
            return vo;
        }).collect(Collectors.toList());
//      3.2封装为page对象
        PageVo pageVo = new PageVo(articleVoList, total);
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult newArticleList() {
        //查询最新文章 封装成ResponseResult返回
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //必须是正式文章
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        //按照浏览量进行排序
        queryWrapper.orderByDesc(Article::getCreateTime);
        //最多只查询10条,这里使用Page查询（可以使用Limit设置）
        Page<Article> page = new Page<>(1, 5);
        page(page, queryWrapper);
        List<Article> records = page.getRecords();

//          调用工具类，赋值给vo对象
        List<HotArticleVo> hotArticleVos = BeanCopyUtils.copyBeanList(records, HotArticleVo.class);
        return ResponseResult.okResult(hotArticleVos);
    }

    public List<ArticleVo> toArticleVoList(List<Article> articleList) {
        return BeanCopyUtils.copyBeanList(articleList, ArticleVo.class);
    }

    /**
     * 检查参数列表，是否输入了内容、简介，标题等关键字
     *
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
