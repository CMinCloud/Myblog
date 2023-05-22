package com.cm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cm.constants.SystemConstants;
import com.cm.domain.dto.PageUsersDto;
import com.cm.domain.dto.UserDto4Status;
import com.cm.domain.entity.*;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.domain.vo.*;
import com.cm.mapper.UserMapper;
import com.cm.mapper.UserRoleMapper;
import com.cm.service.RoleService;
import com.cm.service.UserRoleService;
import com.cm.service.UserService;
import com.cm.utils.BeanCopyUtils;
import com.cm.utils.SecurityUtils;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2023-01-03 12:12:26
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleService roleService;

    @Override
    public ResponseResult userInfo() {
//        从本地线程中获取用户信息
        Long userId = SecurityUtils.getUserId();
        User user = getById(userId);
        userInfoVo userInfo = BeanCopyUtils.copyBean(user, userInfoVo.class);
        return ResponseResult.okResult(userInfo);
    }

    @Override
    @Transactional   //出错进行事务回滚
    public ResponseResult updateUserInfo(userInfoVo userInfoVo) {
        Long userId = userInfoVo.getId();
        User user = getById(userId);
        user.setAvatar(userInfoVo.getAvatar());
        user.setNickName(userInfoVo.getNickName());
        user.setSex(userInfoVo.getSex());
        try {
            updateById(user);
            return ResponseResult.okResult();
        } catch (Exception e) {
//            更新失败暂时抛出系统错误
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
    }

    @Override
    public ResponseResult register(User user) {
//        对数据进行非空判断
        registerCheck(user);
//        加密密码:使用security中的PasswordEncoder进行加密
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
//        存入数据库
        save(user);
        return ResponseResult.okResult();
    }

    //    已登录后获取     用户信息
    @Override
    public ResponseResult SystemUserInfo() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        User user = loginUser.getUser();
        userInfoVo userInfo = BeanCopyUtils.copyBean(user, userInfoVo.class);
        SystemUserVo systemUserVo = new SystemUserVo(loginUser.getPermissions(),
                loginUser.getRoles(), userInfo);
//        user：userInfo.class
        return ResponseResult.okResult(systemUserVo);
    }

    @Override
    public List<String> getPermissions(Long id) {
        return baseMapper.getPermissions(id);
    }

    @Override
    public List<String> getAdminPermissions() {
        return baseMapper.getAdminPermissions();
    }

    @Override
    public List<String> getRoles(Long id) {
        return baseMapper.getSystemRoles(id);
    }

    /**
     * 后台查询用户列表
     *
     * @param pageUsersDto
     * @return
     */
    @Override
    public ResponseResult pageList(PageUsersDto pageUsersDto) {

//        ExecutorService pool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS,
//                new ArrayBlockingQueue<>(1), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
        String status = pageUsersDto.getStatus();
        String userName = pageUsersDto.getUserName();
        String phonenumber = pageUsersDto.getPhonenumber();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
//        判断参数是否不为空：根据用户名进行模糊搜索，进行手机号搜索，进行状态查询
        queryWrapper.like(StringUtils.hasText(userName), User::getUserName, userName);
        queryWrapper.eq(StringUtils.hasText(status), User::getStatus, status);
        queryWrapper.eq(StringUtils.hasText(phonenumber), User::getPhonenumber, phonenumber);
        Page<User> userPage = new Page<>(pageUsersDto.getPageNum(), pageUsersDto.getPageSize());
        Page<User> page = page(userPage, queryWrapper);
        List<UserListVo> userVoList = BeanCopyUtils.copyBeanList(page.getRecords(), UserListVo.class);
//        封装为pageVo对象
        PageVo pageVo = new PageVo(userVoList, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 新增后台用户，同时可以直接关联角色
     *
     * @param user
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult newUser(User user) {
        //        对数据进行非空判断
        registerCheck(user);
//        加密密码:使用security中的PasswordEncoder进行加密
        String encodePassword = passwordEncoder.encode(user.getPassword());
//      在后台添加的用户，需要声明创建信息
        Long adminId = SecurityUtils.getLoginUser().getUser().getId();
        user.setPassword(encodePassword);
        user.setCreateBy(adminId);
        user.setUpdateTime(new Date());
        user.setCreateTime(new Date());
        user.setUpdateBy(adminId);
        boolean isSaved = save(user);
        if (isSaved) {
//            如果关联了角色，添加到角色用户关联表
            List<Long> roleIds = user.getRoleIds();
            if (roleIds.size() != 0) {
                for (Long roleId : roleIds) {
                    userRoleService.save(new UserRole(user.getId(), roleId));
                }
            }
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult delete(Long userId) {
        boolean removed = removeById(userId);
        if (removed)
            return ResponseResult.okResult();
        else
            return ResponseResult.errorResult(555, "用户删除失败");
    }

    @Override
    public ResponseResult userDetail(Long userId) {
//        查询用户详细信息
        User user = getById(userId);
//        查询该用户的角色：一个用户可以拥有多个角色
        List<Long> roleIds = userRoleMapper.listRoleIdsByUserId(userId);
//          查询所有角色表
        List<Role> roles = roleService.list();
        UserDetail4Update userDetailVo = new UserDetail4Update(user, roles, roleIds);
        return ResponseResult.okResult(userDetailVo);
    }

    /**
     * 在后台修改用户，可以修改用户的关联角色
     *
     * @param user
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult updateUserBackGround(User user) {
//        更新用户表
        updateById(user);
//        更新用户角色关联表：先删除所有关联的角色，再重新添加
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
//        先删除
        userRoleService.remove(queryWrapper);
//        再添加
        List<Long> roleIds = user.getRoleIds();
        for (Long roleId : roleIds) {
            userRoleService.save(new UserRole(user.getId(), roleId));

        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult changeStatus(UserDto4Status userDto) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userDto.getUserId()).set("status", userDto.getStatus());
        boolean updated = update(updateWrapper);
        if (updated) {
            return ResponseResult.okResult();
        } else {
            return ResponseResult.errorResult(555, "状态更新失败");
        }
    }

    public boolean registerCheck(User user) {
        String userName = user.getUserName();
        String password = user.getPassword();
        String nickName = user.getNickName();
        String email = user.getEmail();
//        判断用户信息是否为空
        if (!StringUtils.hasText(userName)) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if (!StringUtils.hasText(password)) {
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if (!StringUtils.hasText(nickName)) {
            throw new SystemException(AppHttpCodeEnum.NICKNAME_NOT_NULL);
        }
        if (!StringUtils.hasText(email)) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
//        判断用户信息是否已经存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, userName);
        if (count(queryWrapper) != 0) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        queryWrapper.clear();
        queryWrapper.eq(User::getNickName, nickName);
        if (count(queryWrapper) != 0) {
            throw new SystemException(AppHttpCodeEnum.NICKNAME_EXIST);
        }
        queryWrapper.clear();
        queryWrapper.eq(User::getPhonenumber, user.getPhonenumber());
        if (count(queryWrapper) != 0) {
            throw new SystemException(AppHttpCodeEnum.PHONENUMBER_EXIST);
        }
        queryWrapper.clear();
        queryWrapper.eq(User::getEmail, email);
        if (count(queryWrapper) != 0) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }
        return true;
    }

    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("1234");
        System.out.println(encode);
    }
}

