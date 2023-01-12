package com.cm.controller;


import com.cm.common.aop.LogAnnotation;
import com.cm.domain.entity.SystemException;
import com.cm.domain.entity.User;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.domain.vo.MenuVo;
import com.cm.domain.vo.ResponseResult;
import com.cm.domain.vo.RoutersVo;
import com.cm.domain.entity.Menu;
import com.cm.service.LoginService;
import com.cm.service.MenuService;
import com.cm.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class SystemLoginController {

    @Resource(name = "SystemLoginService")
    private LoginService loginService;

    @Autowired
    private MenuService menuService;

    @PostMapping("/user/login")
    @LogAnnotation(module = "管理员登录模块", operation = "登录操作")
    public ResponseResult login(@RequestBody User user) {
        if (!StringUtils.hasText(user.getUserName())) {
//            提示必须要传输用户名
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        return loginService.login(user);
    }

    @GetMapping("getRouters")
    public ResponseResult<RoutersVo> getRouters(){
        Long userId = SecurityUtils.getUserId();
        //查询menu 结果是tree的形式
        List<MenuVo> menus = menuService.selectRouterMenuTreeByUserId(userId);
        //封装数据返回
        return ResponseResult.okResult(new RoutersVo(menus));
    }
}
