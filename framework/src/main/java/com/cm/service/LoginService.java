package com.cm.service;

import com.cm.domain.vo.ResponseResult;
import com.cm.domain.entity.User;

public interface LoginService {
    ResponseResult login(User user);

    ResponseResult logout();
}
