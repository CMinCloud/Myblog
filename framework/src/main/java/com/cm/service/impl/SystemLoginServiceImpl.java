package com.cm.service.impl;

import com.cm.domain.entity.LoginUser;
import com.cm.domain.entity.SystemException;
import com.cm.domain.entity.User;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.domain.vo.BlogUserLoginVo;
import com.cm.domain.vo.ResponseResult;
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

import java.util.HashMap;
import java.util.Map;

@Service("SystemLoginService")
public class SystemLoginServiceImpl implements LoginService {

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
//        将完整的用户信息存入redis， userid作为key
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
//        这里缓存存入loginUser的原因:包含完整的用户信息+用户权限
//        每次请求都需要判定权限,所以封装在缓存中
        redisCache.setCacheObject("AdminLogin:" + userId, loginUser);
        return ResponseResult.okResult(map);     //返回token值和用户信息
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
        redisCache.deleteObject("AdminLogin:" + userId);
        return ResponseResult.okResult();
    }
}
