package com.cm.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {

    private Integer pageNum;

    private Integer pageSize;

    private String roleName;
    //角色状态（0正常 1停用）
    private String status;

}
