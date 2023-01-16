package com.cm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cm.domain.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 角色和菜单关联表(RoleMenu)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-15 22:41:54
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    List<Long> getMenuIdByRoleId(Long roleId);


}

