package com.cm.service.impl;

import com.cm.domain.entity.LoginUser;
import com.cm.domain.vo.ResponseResult;
import com.cm.domain.entity.User;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.domain.vo.BlogUserLoginVo;
import com.cm.domain.entity.SystemException;
import com.cm.domain.vo.userInfoVo;
import com.cm.service.LoginService;
import com.cm.utils.BeanCopyUtils;
import com.cm.utils.JwtUtil;
import com.cm.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.concurrent.TimeUnit;

@Service("BlogLoginService")
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;


    @Override
    public ResponseResult login(User user) {

//        1、创建authenticationManager的入参
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
//        2、authenticationManager会调用userDetailService接口来进行认证（认证成功将封装用户信息LoginUser在其中）
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
//        如果认证没通过，给出对应的提示
        if (authenticate == null) {
            throw new RuntimeException("登录认证失败,请检查密码!");
        }
//        如果认证通过了，使用userid生成一个jwt，jwt存入ResponseResult中
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userId);
        /**
         * 将完整的用户信息存入redis， userid作为key
         * 这里缓存存入loginUser的原因:包含完整的用户信息+用户权限
         * 每次请求都需要判定权限,所以封装在缓存中，过期实时间为1天，与token过期时间相同
         */
        redisCache.setCacheObject("BlogLogin:" + userId, loginUser, 1, TimeUnit.DAYS);
//        返回BlogUserLoginVo对象
        userInfoVo userInfo = BeanCopyUtils.copyBean(loginUser.getUser(), userInfoVo.class);
        BlogUserLoginVo blogUserLoginVo = new BlogUserLoginVo(userInfo, jwt);
        //返回token值和用户信息
        return new ResponseResult(200, "登录认证成功!", blogUserLoginVo);
    }

    @Override
    public ResponseResult logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new SystemException(AppHttpCodeEnum.NEED_LOGIN);
        }
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        //获取userid
        Long userId = loginUser.getUser().getId();
        //删除redis中的用户信息
        redisCache.deleteObject("BlogLogin:" + userId);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
