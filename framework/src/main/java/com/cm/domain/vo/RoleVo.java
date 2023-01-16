package com.cm.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class RoleVo {
    //角色ID
    private Long id;
    //角色名称
    private String roleName;
    //角色权限字符串
    private String roleKey;
    //显示顺序
    private Integer roleSort;

    private String remark;

    private Date createTime;
    //角色状态（0正常 1停用）
    private String status;
}
