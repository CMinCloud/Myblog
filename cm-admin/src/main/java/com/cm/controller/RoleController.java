package com.cm.controller;


import com.cm.domain.dto.RoleDto;
import com.cm.domain.dto.addRoleDto;
import com.cm.domain.vo.ResponseResult;
import com.cm.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;


    @GetMapping("/list")
    public ResponseResult pageList(RoleDto roleDto) {
        return roleService.pageList(roleDto);
    }

    @PutMapping("/changeStatus")
    public ResponseResult changeStatus(String roleId, String status) {
        return roleService.changeStatus(roleId,status);
    }

    @PostMapping
    public ResponseResult addRole(@RequestBody addRoleDto roleDto){
        return roleService.addRole(roleDto);
    }

    @GetMapping("/{id}")
    public ResponseResult roleDetail(@PathVariable("id") Long id){
        return roleService.roleDetail(id);
    }

    @PutMapping
    public ResponseResult updateRole(@RequestBody addRoleDto roleDto){
        return roleService.updateRole(roleDto);
    }
}
