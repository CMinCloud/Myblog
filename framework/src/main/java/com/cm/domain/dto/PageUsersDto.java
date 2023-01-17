package com.cm.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageUsersDto {

    Integer pageNum;    // 当前页码

    Integer pageSize;   // 每页展示数

    String userName;

    String phonenumber;

    //账号状态（0正常 1停用）
    private String status;
}
