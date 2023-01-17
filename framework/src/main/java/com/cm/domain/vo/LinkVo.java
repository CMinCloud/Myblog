package com.cm.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LinkVo {
    private Long id;
    //网站地址
    private String address;
//    网站描述
    private String description;
    private String logo;
    private String name;

    //审核状态 (0代表审核通过，1代表审核未通过，2代表未审核)
    private String status;
}
