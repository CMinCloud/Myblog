package com.cm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cm.domain.dto.MenuDto;
import com.cm.domain.entity.Menu;
import com.cm.domain.vo.MenuVo;
import com.cm.domain.vo.ResponseResult;

import javax.xml.ws.Response;
import java.util.List;

/**
 * 菜单权限表(Menu)表服务接口
 *
 * @author makejava
 * @since 2023-01-12 18:18:35
 */
public interface MenuService extends IService<Menu> {

    List<MenuVo> selectRouterMenuTreeByUserId(Long userId);

    ResponseResult list(MenuDto menuDto);

    ResponseResult addMenu(Menu menu);

    ResponseResult getMenuById(Long menuId);

    ResponseResult updateMenu(Menu menu);

    ResponseResult deleteMenu(Long id);

    ResponseResult menuTree(Long parentId);

    ResponseResult roleMenuTree(Long id);
}

