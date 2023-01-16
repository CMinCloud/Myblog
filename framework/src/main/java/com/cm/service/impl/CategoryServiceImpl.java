package com.cm.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cm.constants.SystemConstants;
import com.cm.domain.dto.CategoryListDto;
import com.cm.domain.entity.Article;
import com.cm.domain.entity.Category;
import com.cm.domain.entity.SystemException;
import com.cm.domain.entity.Tag;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.domain.vo.PageVo;
import com.cm.domain.vo.ResponseResult;
import com.cm.domain.vo.CategoryVo;
import com.cm.domain.vo.TagVo;
import com.cm.mapper.CategoryMapper;
import com.cm.service.ArticleService;
import com.cm.service.CategoryService;
import com.cm.utils.BeanCopyUtils;
import com.cm.utils.TestFileUtil;
import com.cm.utils.WebUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.swing.StringUIClientPropertyKey;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @Override
    public ResponseResult listAllCategories() {
        List<Category> categoryList = list();
        List<CategoryVo> categoryVoList = BeanCopyUtils.copyBeanList(categoryList, CategoryVo.class);
        return ResponseResult.okResult(categoryVoList);
    }

    @Override
    public ResponseResult listByPage(CategoryListDto categoryDto) {

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        String name = categoryDto.getName();
        String status = categoryDto.getStatus();
//        根据搜索关键字进行匹配：不过这里好像不是模糊查询
        queryWrapper.like(StringUtils.hasText(name), Category::getName, name);
        queryWrapper.eq(StringUtils.hasText(status), Category::getStatus, status);
        queryWrapper.orderByAsc(Category::getId);
        Page<Category> categoryPage = new Page<>(categoryDto.getPageNum(), categoryDto.getPageSize());
        Page<Category> page = page(categoryPage, queryWrapper);
//        获取分页查询结果
        List<Category> records = page.getRecords();
        long total = page.getTotal();
        List<CategoryVo> categoryVoList = toCategoryVoList(records);
        PageVo pageVo = new PageVo(categoryVoList, total);
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getCategoryById(Long categoryId) {
        Category category = getById(categoryId);
        CategoryVo categoryVo = BeanCopyUtils.copyBean(category, CategoryVo.class);
        return ResponseResult.okResult(categoryVo);
    }

    @Override
    public ResponseResult update(Category categoryDto) {
        Category category = getById(categoryDto.getId());
        if (category.getName().equals(categoryDto.getName())
                && category.getDescription().equals(categoryDto.getDescription())
                && category.getStatus().equals(categoryDto.getStatus())
        ) {
//            如果都相同就没必要去操作数据库
            return ResponseResult.okResult();
        }
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        category.setStatus(categoryDto.getStatus());
        if (updateById(category))
            return ResponseResult.okResult();
        return ResponseResult.errorResult(556, "更新分类失败");
    }

    @Override
    public ResponseResult addCategory(Category category) {
        if (!StringUtils.hasText(category.getName())
                || !StringUtils.hasText(category.getDescription())) {
            throw new SystemException(AppHttpCodeEnum.KEYWORDS_NOT_NULL);
        }
        if (save(category))
            return ResponseResult.okResult();
        else
            return ResponseResult.errorResult(555, "新增分类失败");
    }

    @Override
    public ResponseResult deleteById(List<Long> ids) {
        boolean deleted = false;
        if (ids.size() == 1) {
//            只删除一个标签
            deleted = removeById(ids.get(0));
        }
//        批量删除
        deleted = removeByIds(ids);
        if (deleted)
            return ResponseResult.okResult();
        else
            return ResponseResult.errorResult(556, "删除分类失败");
    }

    @Override
    public void export(HttpServletResponse response) {
        try {
            //设置下载文件的请求头
            WebUtils.setDownLoadHeader("Category.xlsx", response);
//        获取EasyExcel模板对象
            List<CategoryVo> categoryVos = BeanCopyUtils.copyBeanList(list(), CategoryVo.class);
            //把数据写入到Excel中
            EasyExcel.write(response.getOutputStream(), CategoryVo.class).autoCloseStream(Boolean.FALSE).sheet("分类导出")
                    .doWrite(categoryVos);
        } catch (IOException e) {
            response.reset();   //清空response中写入的数据
            //如果出现异常也要响应json
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
            WebUtils.renderString(response, JSON.toJSONString(result));
        }
    }

    private List<CategoryVo> toCategoryVoList(List<Category> categoryList) {
        return BeanCopyUtils.copyBeanList(categoryList, CategoryVo.class);
    }
}

