package com.cm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cm.domain.entity.Comment;
import com.cm.domain.params.PageParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 评论表(Comment)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-04 09:44:31
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {


//    查询评论列表（文章评论和友链评论）
    List<Comment> getCommentList(PageParam pageParam);

//    查询评论总数
    Long getCommentTotal(PageParam pageParam);

}

