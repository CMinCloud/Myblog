package com.cm.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ArticleVo {

    private Long id;
    //标题
    private String title;
    //文章摘要
    private String summary;
    //所属分类名称
    private String categoryName;
    //缩略图
    private String thumbnail;
    //访问量
    private Long viewCount;
    //创建时间
//    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

//    创建者id
    private Long createBy;

//    分页查询文章列表新增属性
    private Long categoryId;

    private String content;

   //是否置顶（0否，1是）
    private String isTop;
    //状态（0已发布，1草稿）
    private String status;
//    获取用户名称
    private String author;


}
