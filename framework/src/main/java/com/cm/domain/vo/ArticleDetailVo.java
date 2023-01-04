package com.cm.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ArticleDetailVo {

    private Long id;
    //所属分类id
    private Long categoryId;

    private String categoryName;
    //文章内容
    private String content;

    private Date createTime;
    //是否允许评论 1是，0否
    private String isComment;
    //标题
    private String title;
    //访问量
    private Long viewCount;

}
