package com.cm.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryListDto {

    private Integer pageNum;
    private Integer pageSize;
    private String name;
    //状态0:正常,1禁用
    private String status;

}
