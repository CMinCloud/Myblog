package com.cm.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagListDto {
    private Long id;

    private String name;

    private  String remark;  //备注：用于根据关键字模糊查询
}
