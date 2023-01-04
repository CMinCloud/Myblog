package com.cm.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 文章表(Article)表实体类
 *
 * @author makejava
 * @since 2022-12-31 16:50:40
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@TableName("cm_article")
//@Accessors(chain = true)  //lombok注解方法，，生成setter方法返回this（也就是返回的是对象），代替了默认的返回void，方便lambda编程
public class Article  {
    
    private Long id;
    //标题
    private String title;
    //文章内容
    private String content;
    //文章摘要
    private String summary;
    //所属分类id
    private Long categoryId;

//    分类名称，在数据表中实际不存在，但在封装数据时为了方便所以在这里填加该属性
    @TableField(exist = false)
    private String categoryName;
    //缩略图
    private String thumbnail;
    //是否置顶（0否，1是）
    private String isTop;
    //状态（0已发布，1草稿）
    private String status;
    //访问量
    private Long viewCount;
    //是否允许评论 1是，0否
    private String isComment;
    
    private Long createBy;
    
    private Date createTime;
    
    private Long updateBy;
    
    private Date updateTime;
    //删除标志（0代表未删除，1代表已删除）
    private Integer delFlag;

}

