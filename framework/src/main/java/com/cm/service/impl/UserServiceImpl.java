package com.cm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cm.domain.entity.LoginUser;
import com.cm.domain.entity.SystemException;
import com.cm.domain.entity.User;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.domain.vo.ResponseResult;
import com.cm.domain.vo.SystemUserVo;
import com.cm.domain.vo.userInfoVo;
import com.cm.mapper.UserMapper;
import com.cm.service.UserService;
import com.cm.utils.BeanCopyUtils;
import com.cm.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

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
        return true;
    }
}

