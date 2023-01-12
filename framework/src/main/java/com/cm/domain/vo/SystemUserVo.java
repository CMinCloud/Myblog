package com.cm.domain.vo;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SystemUserVo {

    //    存储用户权限信息
    private List<String> permissions;

    //    存储用户角色
    private List<String> roles;

//    存储用户信息
    private userInfoVo user;

    public SystemUserVo(List<String> permissions, List<String> roles, userInfoVo user) {
        this.permissions = permissions;
        this.roles = roles;
        this.user = user;
    }
}
