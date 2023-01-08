package com.cm.controller;

import com.cm.domain.entity.Comment;
import com.cm.domain.entity.SystemException;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.domain.params.PageParam;
import com.cm.domain.vo.ResponseResult;
import com.cm.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
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
    public ResponseResult linkCommentList(Integer pageNum, Integer pageSize) {
        PageParam commentParam = new PageParam(null, pageNum, pageSize, "1");
        return commentService.commentList(commentParam);

    }

}
