package com.cm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cm.domain.entity.Comment;
import com.cm.domain.params.PageParam;
import com.cm.domain.vo.ResponseResult;

/**
 * 评论表(Comment)表服务接口
 *
 * @author makejava
 * @since 2023-01-04 09:44:31
 */
public interface CommentService extends IService<Comment> {

    ResponseResult commentList(PageParam commentParam);

    ResponseResult addComment(Comment comment);
}

