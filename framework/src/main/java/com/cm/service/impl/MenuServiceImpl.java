package com.cm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cm.domain.entity.Menu;
import com.cm.domain.vo.MenuVo;
import com.cm.domain.vo.ResponseResult;
import com.cm.mapper.MenuMapper;
import com.cm.service.MenuService;
import com.cm.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author makejava
 * @since 2023-01-12 18:18:35
 */
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {


    @Override
    public List<MenuVo> selectRouterMenuTreeByUserId(Long userId) {
        List<Menu> routers;
        if (userId == 1L) {
//            查询管理员的菜单面板
            routers = baseMapper.getAdminRouters();
        }else {
            routers = baseMapper.getRoutersByUserId(userId);
        }
        List<MenuVo> menuVos = builderMenuTree(routers);
        List<MenuVo> menuVoList = menuVos.stream().map(menuVo -> {
            setChildren(menuVo);
            return menuVo;
        }).collect(Collectors.toList());
//        根据id查询系统用户的菜单面板
        return menuVoList;
    }

    //    将List<Menu>封装为Vo并填充children
    private List<MenuVo> builderMenuTree(List<Menu> menus) {
        List<MenuVo> menuVos = menus.stream()
                .map(menu -> BeanCopyUtils.copyBean(menu, MenuVo.class)).collect(Collectors.toList());
        return menuVos;
    }

    private void setChildren(MenuVo menuVo) {
        List<Menu> children = baseMapper.getRoutersByParentId(menuVo.getId());
        List<MenuVo> voList = builderMenuTree(children);
        menuVo.setChildren(voList);
    }


}

