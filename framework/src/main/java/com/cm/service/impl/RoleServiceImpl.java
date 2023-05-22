package com.cm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cm.constants.SystemConstants;
import com.cm.domain.dto.RoleDto;
import com.cm.domain.dto.addRoleDto;
import com.cm.domain.entity.*;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.domain.vo.*;
import com.cm.mapper.MenuMapper;
import com.cm.mapper.RoleMapper;
import com.cm.service.MenuService;
import com.cm.service.RoleMenuService;
import com.cm.service.RoleService;
import com.cm.utils.BeanCopyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 角色信息表(Role)表服务实现类
 *
 * @author makejava
 * @since 2023-01-15 21:15:54
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RoleMenuService roleMenuService;

    @Override
    public ResponseResult pageList(RoleDto roleDto) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        String roleName = roleDto.getRoleName();
        String status = roleDto.getStatus();
//        根据搜索关键字进行匹配：不过这里好像不是模糊查询
        queryWrapper.like(StringUtils.hasText(roleName), Role::getRoleName, roleName);
        queryWrapper.eq(StringUtils.hasText(status), Role::getStatus, status);
        queryWrapper.orderByAsc(Role::getRoleSort);
        Page<Role> rolePage = new Page<>(roleDto.getPageNum(), roleDto.getPageSize());
        Page<Role> page = page(rolePage, queryWrapper);
//        获取分页查询结果
        List<Role> records = page.getRecords();
        long total = page.getTotal();
        List<RoleVo> roleVoList = toRoleVoList(records);
        PageVo pageVo = new PageVo(roleVoList, total);
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult changeStatus(String roleId, String status) {
        if (SystemConstants.STATUS_NORMAL.equals(status)) {
            status = SystemConstants.STATUS_DRAFT;
        } else
            status = SystemConstants.STATUS_NORMAL;
        Boolean isChanged = baseMapper.changeStatusByRoleId(Long.valueOf(roleId), status);
        if (isChanged)
            return ResponseResult.okResult();
        else
            return ResponseResult.errorResult(555, "状态修改失败");
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult addRole(addRoleDto roleDto) {
//        存入role角色表
        Role role = BeanCopyUtils.copyBean(roleDto, Role.class);
        boolean isSaved = save(role);
//        存入role_menu关联表
        if (isSaved) {
            List<Long> menuIds = roleDto.getMenuIds();
            for (Long menuId : menuIds) {
                roleMenuService.save(new RoleMenu(role.getId(), menuId));
            }
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult roleDetail(Long id) {
        Role role = getById(id);
        RoleVo roleVo = BeanCopyUtils.copyBean(role, RoleVo.class);
        return ResponseResult.okResult(roleVo);
    }

    //    修改角色信息
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult updateRole(addRoleDto roleDto) {
        UpdateWrapper<Role> updateWrapper = new UpdateWrapper<>();

        updateWrapper.eq("id", roleDto.getId())
                .set("role_name", roleDto.getRoleName())
                .set("role_key", roleDto.getRoleKey())
                .set("role_sort", roleDto.getRoleSort())
                .set("status", roleDto.getStatus())
                .set("remark", roleDto.getRemark());
        boolean updated = update(updateWrapper);
        if (updated) {
//            修改role_menu中间表：先删除全部，再新增
            roleMenuService.remove(new QueryWrapper<RoleMenu>().eq("role_id", roleDto.getId()));
            for (Long menuId : roleDto.getMenuIds()) {
//                最好检查是否未修改
                roleMenuService.save(new RoleMenu(roleDto.getId(), menuId));
            }
        }
        return ResponseResult.okResult();
    }

    /**
     * 删除对应角色:逻辑删除，所以不用删除其权限列表
     *
     * @param roleId
     * @return
     */
    @Override
    public ResponseResult deleteRole(Long roleId) {
        boolean removed = removeById(roleId);
        if (removed) {
            return ResponseResult.okResult();
        } else {
            return ResponseResult.errorResult(555, "删除角色失败");
        }
    }

    @Override
    public ResponseResult listAllRole() {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getStatus, SystemConstants.STATUS_NORMAL);
        List<Role> roles = list(queryWrapper);
        List<RoleVo> voList = BeanCopyUtils.copyBeanList(roles, RoleVo.class);
        return ResponseResult.okResult(voList);
    }


    private List<RoleVo> toRoleVoList(List<Role> roleList) {
        return BeanCopyUtils.copyBeanList(roleList, RoleVo.class);
    }

}

