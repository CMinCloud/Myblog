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
}
