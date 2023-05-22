package com.cm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cm.domain.entity.*;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.domain.vo.ResponseResult;
import com.cm.mapper.FollowMapper;
import com.cm.service.ArticleService;
import com.cm.service.FollowService;
import com.cm.service.UserService;
import com.cm.utils.RedisCache;
import com.cm.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RedisCache redisCache;
    /**
     * 1：新增 关系到follow表
     * 2：将该粉丝添加到博主的redis缓存(便于推送)
     * @param articleId
     * @param isFollow
     * @return
     */
    @Override
    public ResponseResult follow(Long articleId, Boolean isFollow) {
//        获取当前用户信息
        Long userId = SecurityUtils.getLoginUser().getUser().getId();
//        获取文章作者id
        Long followUserId;
        try{
            followUserId = articleService.getById(articleId).getCreateBy();
        }catch (Exception e){
            throw new SystemException(AppHttpCodeEnum.USER_NOT_EXIST);
        }
//        1.关注新用户：
        if (isFollow) {
//            1.1 自己不能关注自己
            if(userId.equals(followUserId)){
                throw new SystemException(AppHttpCodeEnum.FOLLOWSELF);
            }
//            1.1目标两个用户必须都存在才能关注
            if (Objects.isNull(userService.getById(followUserId))) {
                throw new SystemException(AppHttpCodeEnum.USER_NOT_EXIST);
            }
//            1.2检查是否已关注
            QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                    .eq("follow_user_id", followUserId);
            Follow follow = getOne(queryWrapper);
            if(!Objects.isNull(follow)){
                throw new SystemException(AppHttpCodeEnum.REDUPLICATED_FOLLOW);
            }
//            未关注，可以插入
            Follow newFollow = new Follow();
            newFollow.setUserId(userId);
            newFollow.setFollowUserId(followUserId);
            save(newFollow);
//            1.3 添加到博主的粉丝缓存
            redisCache.addFans2Redis(followUserId,userId);
        } else {
//        2.取消关注：
            QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                    .eq("follow_user_id", followUserId);
            remove(queryWrapper);
//            同时清除缓存
            redisCache.deleteFans4Redis(followUserId,userId);
        }
        return ResponseResult.okResult();
    }

    /**
     * 检查当前登录用户是否是该文章作者的粉丝
     * @param articleId
     * @return
     */
    @Override
    public ResponseResult checkFollow( Long articleId) {
//        获取作者id
        Article article = articleService.getById(articleId);
//        当前登录用户的id
        Long id = SecurityUtils.getLoginUser().getUser().getId();
        QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",id).eq("follow_user_id",article.getCreateBy());
        Follow follow = getOne(queryWrapper);
        if(Objects.isNull(follow)){
            return ResponseResult.okResult(false);
        }
//        已关注
        return ResponseResult.okResult(true);
    }

}
