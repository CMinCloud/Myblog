package com.cm.domain.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageParam {

    Long Id;  //  id:可以用于articleId和commentId，categoryId

    Integer pageNum;    // 当前页码

    Integer pageSize;   // 每页展示数

    String commentType;    // 文章评论"0"，友链评论"1"

    public PageParam(Long id, Integer pageNum, Integer pageSize) {
        Id = id;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public PageParam(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
