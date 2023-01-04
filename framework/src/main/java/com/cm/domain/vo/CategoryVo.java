package com.cm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类表(Category)表实体类
 *
 * @author makejava
 * @since 2023-01-02 10:56:11
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryVo {
    
    private Long id;
    //分类名
    private String name;
}

