package com.cm.domain.vo;

import com.cm.domain.entity.Role;
import com.cm.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetail4Update {

    private User user;   //这里最好是返回一个只包含对应参数的vo对象

    private List<Role> roles;       //所有可选角色信息

    private List<Long> roleIds;     //当前用户所关联的角色


}
