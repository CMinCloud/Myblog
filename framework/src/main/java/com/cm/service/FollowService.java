package com.cm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cm.domain.entity.Follow;
import com.cm.domain.vo.ResponseResult;

public interface FollowService extends IService<Follow> {
    ResponseResult follow(Long followUserId, Boolean isFollow);

    ResponseResult checkFollow( Long articleId);
}
