package com.cm.domain.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleListParam {

    Integer pageNum;    // 当前页码

    Integer pageSize;   // 每页展示数

    Long categoryId;  //分类id
}
