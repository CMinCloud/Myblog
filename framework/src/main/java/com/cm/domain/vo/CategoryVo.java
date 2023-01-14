package com.cm.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
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

    @ExcelProperty("id")
    private Long id;
    //分类名
    @ExcelProperty("分类名")
    private String name;
    //描述
    @ExcelProperty("描述")
    private String description;
    //    是否可用：0可用，1不可用
    @ExcelProperty("状态0:正常,1禁用")
    private String status;
}

