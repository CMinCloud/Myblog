package com.cm.service;

import com.cm.domain.entity.ResponseResult;
import com.cm.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

public interface LoginService {
    ResponseResult login(User user);

    ResponseResult logout();
}
