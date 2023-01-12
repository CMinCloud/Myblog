package com.cm.controller;


import com.cm.common.aop.LogAnnotation;
import com.cm.domain.vo.ResponseResult;
import com.cm.domain.entity.User;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.domain.entity.SystemException;
import com.cm.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class LoginController {

    @Resource(name = "BlogLoginService")
    private LoginService loginService;


    @PostMapping("/login")
    @LogAnnotation(module = "登录模块",operation = "登录操作")
    public ResponseResult login(@RequestBody User user){
        if(!StringUtils.hasText(user.getUserName())){
//            提示必须要传输用户名
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        return loginService.login(user);
    }

    @PostMapping("/logout")
    public ResponseResult logout(){
        return loginService.logout();
    }
}
