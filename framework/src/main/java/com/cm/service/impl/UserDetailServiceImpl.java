package com.cm.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cm.domain.entity.LoginUser;
import com.cm.domain.entity.User;
import com.cm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class UserDetailServiceImpl implements UserDetailsService {


    @Autowired
    private UserService userService;


//    authenticate进行用户认证(会调用userDetailService接口来进行认证)

    /**
     * 注意这里返回值是UserDetails，所以我们还要重写UserDetails这个接口
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//      从数据库中查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, username);
        User user = userService.getOne(queryWrapper);
//        没有查询到用户
        if (Objects.isNull(user)) {       // 和 == null 没区别
//            这个异常 最后会被ExceptionTranslationalFilter给捕获
            throw new UsernameNotFoundException("用户不存在");
        } else if ("1".equals(user.getStatus())) {
            throw new UsernameNotFoundException("该账号已被停用,请联系管理员");
        }

/*//        暂时写死
//        根据userId获取用户权限
            List<String> permissions = menuMapper.selectPermsByUser_Id(user.getId());*/
//        todo 获取用户权限
        return new LoginUser(user);
    }
}

