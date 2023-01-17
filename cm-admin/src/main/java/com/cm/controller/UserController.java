package com.cm.controller;


import com.cm.domain.dto.PageUsersDto;
import com.cm.domain.dto.UserDto4Status;
import com.cm.domain.entity.User;
import com.cm.domain.vo.ResponseResult;
import com.cm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public ResponseResult pageList(PageUsersDto pageUsersDto) {
        return userService.pageList(pageUsersDto);
    }


    //    一般通过注册完成；  这里用于新增管理员用户
    @PostMapping
    public ResponseResult addUser(@RequestBody User user) {
        return userService.newUser(user);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteUser(@PathVariable("id") Long userId) {
        return userService.delete(userId);
    }

    @GetMapping("/{id}")
    public ResponseResult getUserDetail(@PathVariable("id") Long userId) {
        return userService.userDetail(userId);
    }

    @PutMapping
    public ResponseResult updateUser(@RequestBody User user) {
        return userService.updateUserBackGround(user);
    }

    @PutMapping("/changeStatus")
    public ResponseResult changeStatus(@RequestBody UserDto4Status userDto){
        return userService.changeStatus(userDto);
    }


}
