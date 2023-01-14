package com.cm.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TagVo {

    private Long id;
    //标签名
    private String name;

    //备注
    private String remark;
}
