package com.cm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cm.domain.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 菜单权限表(Menu)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-12 18:18:35
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

//    查询管理员的父类路由
    List<Menu> getAdminRouters();

//    根据父类路由查询当前路由
    List<Menu> getRoutersByParentId(Long parentId);

//    根据id查询父类路由
    List<Menu> getRoutersByUserId(Long userId);

}

