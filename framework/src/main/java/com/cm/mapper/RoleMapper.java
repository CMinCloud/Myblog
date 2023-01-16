package com.cm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cm.domain.entity.Role;
import org.apache.ibatis.annotations.Mapper;


/**
 * 角色信息表(Role)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-15 21:15:54
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {


    Boolean changeStatusByRoleId(Long roleId,String status);



}

