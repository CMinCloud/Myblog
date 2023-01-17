package com.cm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cm.domain.dto.PageUsersDto;
import com.cm.domain.dto.UserDto4Status;
import com.cm.domain.entity.User;
import com.cm.domain.vo.ResponseResult;
import com.cm.domain.vo.userInfoVo;

import java.util.List;

/**
 * 用户表(User)表服务接口
 *
 * @author makejava
 * @since 2023-01-03 12:12:26
 */
public interface UserService extends IService<User> {

    ResponseResult userInfo();

    ResponseResult updateUserInfo(userInfoVo userInfoVo);

    ResponseResult register(User user);

    ResponseResult SystemUserInfo();

    List<String> getPermissions(Long id);

    List<String> getAdminPermissions();

    List<String> getRoles(Long id);

    ResponseResult pageList(PageUsersDto pageUsersDto);

    ResponseResult newUser(User user);

    ResponseResult delete(Long userId);

    ResponseResult userDetail(Long userId);

    ResponseResult updateUserBackGround(User user);

    ResponseResult changeStatus( UserDto4Status userDto);
}

