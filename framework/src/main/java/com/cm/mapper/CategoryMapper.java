package com.cm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cm.domain.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 分类表(Category)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-02 10:57:26
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    List<Category> selectCategoryList();
}

