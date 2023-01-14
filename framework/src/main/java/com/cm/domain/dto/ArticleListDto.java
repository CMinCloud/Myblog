package com.cm.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArticleListDto {

    private Integer pageNum;    // 当前页码

    private Integer pageSize;   // 每页展示数

    private String title;

    private String summary;
}
