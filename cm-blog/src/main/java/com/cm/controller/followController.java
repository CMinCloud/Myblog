package com.cm.controller;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cm.domain.entity.Follow;
import com.cm.domain.vo.ResponseResult;
import com.cm.mapper.FollowMapper;
import com.cm.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/follow")
public class followController  {

    @Autowired
    private FollowService followService;

//    关注或取关
    @PutMapping("/{id}/{isFollow}")
    public ResponseResult follow(@PathVariable("id") Long followUserId,@PathVariable("isFollow") Boolean isFollow){
        return followService.follow(followUserId,isFollow);
    }


    @GetMapping("checkFollow/{articleId}")
    public ResponseResult checkFollow(@PathVariable("articleId") Long articleId){
        return followService.checkFollow(articleId);
    }

}
