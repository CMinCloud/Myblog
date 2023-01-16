package com.cm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cm.domain.entity.RoleMenu;
import com.cm.mapper.RoleMenuMapper;
import com.cm.service.RoleMenuService;
import org.springframework.stereotype.Service;

/**
 * 角色和菜单关联表(RoleMenu)表服务实现类
 *
 * @author makejava
 * @since 2023-01-15 22:41:54
 */
@Service("roleMenuService")
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

}

