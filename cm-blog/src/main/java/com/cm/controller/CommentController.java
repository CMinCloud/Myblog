package com.cm.controller;

import com.cm.domain.entity.Comment;
import com.cm.domain.entity.SystemException;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.domain.params.PageParam;
import com.cm.domain.vo.ResponseResult;
import com.cm.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@Api(tags = "评论", value = "评论相关接口")         //controlle描述配置
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/commentList")
    public ResponseResult commentList(Long articleId, Integer pageNum, Integer pageSize) {
        PageParam commentParam = new PageParam(articleId, pageNum, pageSize, "0");
        return commentService.commentList(commentParam);
    }

    //    发表文章评论  ，   发表友链评论
    @PostMapping
    public ResponseResult addComment(@RequestBody Comment comment) {
        return commentService.addComment(comment);
    }

    //    请求友联评论列表
    @GetMapping("/linkCommentList")
    @ApiOperation(value = "友链评论列表", notes = "获取一页友链评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页号"),
            @ApiImplicitParam(name = "pageSize", value = "每页长度")
    })
    public ResponseResult linkCommentList(Integer pageNum, Integer pageSize) {
        PageParam commentParam = new PageParam(null, pageNum, pageSize, "1");
        return commentService.commentList(commentParam);

    }

}
