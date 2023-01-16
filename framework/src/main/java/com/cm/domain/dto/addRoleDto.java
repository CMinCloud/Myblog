package com.cm.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class addRoleDto {

    private Long id;
    //角色名称
    private String roleName;
    //角色权限字符串
    private String roleKey;
    //显示顺序
    private Integer roleSort;
    //角色状态（0正常 1停用）
    private String status;
    //备注
    private String remark;
//    菜单权限
    private List<Long> menuIds;
}
