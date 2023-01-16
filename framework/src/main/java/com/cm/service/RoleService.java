package com.cm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cm.domain.dto.RoleDto;
import com.cm.domain.dto.addRoleDto;
import com.cm.domain.entity.Role;
import com.cm.domain.vo.ResponseResult;

/**
 * 角色信息表(Role)表服务接口
 *
 * @author makejava
 * @since 2023-01-15 21:15:54
 */
public interface RoleService extends IService<Role> {

    ResponseResult pageList(RoleDto roleDto);

    ResponseResult changeStatus(String roleId, String status);

    ResponseResult addRole(addRoleDto roleDto);

    ResponseResult roleDetail(Long id);

    ResponseResult updateRole(addRoleDto roleDto);
}

