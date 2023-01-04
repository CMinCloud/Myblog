package com.cm.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
/**
 * 分类表(Category)表实体类
 *
 * @author makejava
 * @since 2023-01-02 10:56:11
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@TableName("cm_category")
public class Category  {
    
    private Long id;
    //分类名
    private String name;
    //父分类id，如果没有父分类为-1
    private Long pid;
    //描述
    private String description;
    //状态0:正常,1禁用
    private String status;
    
    private Long createBy;
    
    private Date createTime;
    
    private Long updateBy;
    
    private Date updateTime;
    //删除标志（0代表未删除，1代表已删除）
    private Integer delFlag;

}

