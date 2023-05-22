package com.cm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cm.domain.dto.MenuDto;
import com.cm.domain.entity.Menu;
import com.cm.domain.entity.SystemException;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.domain.vo.MenuVo;
import com.cm.domain.vo.MenuVo4Role;
import com.cm.domain.vo.ResponseResult;
import com.cm.domain.vo.RoleMenuTree;
import com.cm.mapper.MenuMapper;
import com.cm.mapper.RoleMenuMapper;
import com.cm.service.MenuService;
import com.cm.utils.BeanCopyUtils;
import com.cm.utils.RedisCache;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.xml.ws.Response;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author makejava
 * @since 2023-01-12 18:18:35
 */
@Service("menuService")
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private RedisCache redisCache;


    /**
     * 获取管理员的菜单路由：常访问，变动小
     * 查询方式：从数据库中查询
     *
     * @param userId
     * @return
     */
    @Override
    public List<MenuVo> selectRouterMenuTreeByUserId(Long userId) {
        String redisKey = RedisCache.AdminRouterKey + userId;
/*        //        先去redis中查看
        List<MenuVo> result = redisCache.getCacheObject(redisKey);
        if (result.size() != 0) {
            return result;
        }*/
        List<Menu> routers;
        if (userId == 1L) {
//            查询管理员的菜单面板
            routers = baseMapper.getAdminRouters();
        } else {
            routers = baseMapper.getRoutersByUserId(userId);
        }
        List<MenuVo> menuVos = builderMenuTree(routers);
        List<MenuVo> menuVoList = menuVos.stream().map(menuVo -> {
            setChildren(menuVo);
            return menuVo;
        }).collect(Collectors.toList());
//        将结果存入Redis
        redisCache.setCacheObject(redisKey, menuVoList);
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

    @Override
    public ResponseResult list(MenuDto menuDto) {
        String status = menuDto.getStatus();
        String menuName = menuDto.getMenuName();
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(status), Menu::getStatus, status)
                .eq(StringUtils.hasText(menuName), Menu::getMenuName, menuName)
                .orderByAsc(Menu::getParentId, Menu::getOrderNum);
        List<Menu> list = list(queryWrapper);
        return ResponseResult.okResult(list);
    }

    @Override
    public ResponseResult addMenu(Menu menu) {
//        检查关键字
        if (checkKeyWordsNonNull(menu)) {
            save(menu);
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getMenuById(Long menuId) {
        Menu menu = getById(menuId);
        return ResponseResult.okResult(menu);
    }

    @Override
    public ResponseResult updateMenu(Menu menu) {
//        检查修改后的parentId是否和menuId一样：父菜单不能成为当前菜单
        if (menu.getId().equals(menu.getParentId())) {
            return ResponseResult.errorResult(500, "修改菜单'写博文'失败，上级菜单不能选择自己");
        }
        updateById(menu);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteMenu(Long id) {
//        检查该菜单是否有子菜单
        Long count = baseMapper.getRouterCountByParentId(id);
        if (count != 0) {
//        有子菜单：不允许删除
            return ResponseResult.errorResult(500, "存在子菜单不允许删除");
        }
//        考虑子菜单被禁用的情况下
//        无子菜单:可以删除
        removeById(id);
        return ResponseResult.okResult();
    }

    private Boolean checkKeyWordsNonNull(Menu menu) {
        if (Objects.isNull(menu.getParentId())
                || !StringUtils.hasText(menu.getMenuName())
                || Objects.isNull(menu.getOrderNum())
                || !StringUtils.hasText(menu.getPath())) {
            throw new SystemException(AppHttpCodeEnum.KEYWORDS_NOT_NULL);
        }

        return Boolean.TRUE;
    }


    @Override
    public ResponseResult menuTree(Long parentId) {
//                先找出parentId为0的menu
        List<MenuVo4Role> menuVo = baseMapper.selectMenuByParentId(parentId);
        if (menuVo == null) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
//        封装vo对象中的children
        List<MenuVo4Role> VoList = menuVo.stream().map(vo -> {
            fillChildren(vo);
            return vo;
        }).collect(Collectors.toList());
        return ResponseResult.okResult(VoList);
    }

    @Override
    public ResponseResult roleMenuTree(Long roleId) {
//        获取当前代理对象
        MenuService menuService = (MenuService) AopContext.currentProxy();
//        获取菜单树
        ResponseResult responseResult = menuService.menuTree(0L);
        List<MenuVo4Role> VoList = (List<MenuVo4Role>) responseResult.getData();
//        获取角色对应权限列表
        List<Long> checkMenuIds = getCheckedMenuIds(roleId);
        RoleMenuTree result = new RoleMenuTree(VoList, checkMenuIds);
        return ResponseResult.okResult(result);
    }

    private List<Long> getCheckedMenuIds(Long roleId) {
        List<Long> ids;
        if (roleId == 1L) {
            ids = baseMapper.getAllIds();
        } else {
            ids = roleMenuMapper.getMenuIdByRoleId(roleId);
        }
        return ids;
    }


    //    这个比较耗费内存，建议直接存储在redis中
    private void fillChildren(MenuVo4Role vo) {
        List<MenuVo4Role> children = baseMapper.selectMenuByParentId(vo.getId());
        if (children.size() != 0) {
            vo.setChildren(children);
            for (MenuVo4Role child : children) {
                fillChildren(child);  //递归填充每一个menu的children直到为null
            }
        }
    }


}

