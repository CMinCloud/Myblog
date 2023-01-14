package com.cm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cm.domain.dto.CategoryListDto;
import com.cm.domain.entity.Category;
import com.cm.domain.vo.ResponseResult;

/**
 * 分类表(Category)表服务接口
 *
 * @author makejava
 * @since 2023-01-02 10:57:11
 */
public interface CategoryService extends IService<Category> {

    ResponseResult getCategoryList();

    ResponseResult listAllCategories();

    ResponseResult listByPage(CategoryListDto categoryListDto);
}

