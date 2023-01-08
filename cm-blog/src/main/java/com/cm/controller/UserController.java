package com.cm.controller;

import com.cm.domain.entity.User;
import com.cm.domain.vo.ResponseResult;
import com.cm.domain.vo.userInfoVo;
import com.cm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/userInfo")
    public ResponseResult userInfo(){
        return userService.userInfo();
    }

    @PutMapping("/userInfo")
    public ResponseResult updateUserInfo(@RequestBody userInfoVo userInfoVo){
        return userService.updateUserInfo(userInfoVo);
    }

//    用户注册
    @PostMapping("/register")
    public ResponseResult register(@RequestBody User user){
        return userService.register(user);
    }

}
