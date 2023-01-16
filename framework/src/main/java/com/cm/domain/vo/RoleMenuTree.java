package com.cm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


//  更新角色菜单信息
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleMenuTree {
    //    菜单树
    List<MenuVo4Role> menus;
    //    角色所关联的菜单权限id列表
    List<Long> checkedKeys;
}
