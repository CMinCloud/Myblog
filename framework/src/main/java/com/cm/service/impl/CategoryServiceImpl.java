package com.cm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cm.constants.SystemConstants;
import com.cm.domain.entity.Article;
import com.cm.domain.entity.Category;
import com.cm.domain.vo.ResponseResult;
import com.cm.domain.vo.CategoryVo;
import com.cm.mapper.CategoryMapper;
import com.cm.service.ArticleService;
import com.cm.service.CategoryService;
import com.cm.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 分类表(Category)表服务实现类
 *
 * @author makejava
 * @since 2023-01-02 10:58:37
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private ArticleService articleService;

/*
//    使用方法注入依赖代替使用@Autowire注入
    public ArticleService articleService(){
        return SpringUtils.getBean(ArticleService.class);
    }
*/

    @Override
    public ResponseResult getCategoryList() {
//        一：使用自己写的sql完成
//        List<Category> categoryList = categoryMapper.selectCategoryList();


//        二：使用MP的接口实现
//            1、查询 已发布的文章id 的categoryId
        LambdaQueryWrapper<Article> articleQueryWrapper = new LambdaQueryWrapper<>();
        articleQueryWrapper.select(Article::getCategoryId)
                .eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL)
                .eq(Article::getDelFlag, SystemConstants.ARTICLE_STATUS_NORMAL);
//        使用Lambda表达式封装id类型为Long（原先为Object）,封装为set还可以去重
        Set<Long> ids = articleService.listObjs(articleQueryWrapper).stream()
                .map(id -> (Long) id).collect(Collectors.toSet());

//          2.根据categoryId查询category并封装为vo对象
        List<Category> categories = listByIds(ids);
//        根据status属性过滤
        List<Category> categoryList = categories.stream()
                .filter(category -> SystemConstants.STATUS_NORMAL.equals(category.getStatus()))
                .collect(Collectors.toList());
//        转换为vo对象
        List<CategoryVo> categoryVoList = BeanCopyUtils.copyBeanList(categoryList, CategoryVo.class);
        return ResponseResult.okResult(categoryVoList);
    }
}

