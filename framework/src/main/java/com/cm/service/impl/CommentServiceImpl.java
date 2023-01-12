package com.cm.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cm.constants.SystemConstants;
import com.cm.domain.entity.Comment;
import com.cm.domain.entity.SystemException;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.domain.params.PageParam;
import com.cm.domain.vo.CommentVo;
import com.cm.domain.vo.PageVo;
import com.cm.domain.vo.ResponseResult;
import com.cm.mapper.CommentMapper;
import com.cm.service.CommentService;
import com.cm.service.UserService;
import com.cm.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.util.List;

import static com.cm.constants.SystemConstants.COMMENT_ROOT;

/**
 * 评论表(Comment)表服务实现类
 *
 * @author makejava
 * @since 2023-01-04 09:44:31
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {


    @Autowired
    private UserService userService;


    //    查询评论列表使用一个统一的sql来完成
    @Override
    public ResponseResult commentList(PageParam commentParam) {
/*        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
//        查询得到对应articleId的评论
        Long articleId = commentParam.getId();
        queryWrapper.eq(Comment::getArticleId, articleId)
                .eq(Comment::getDelFlag, SystemConstants.STATUS_NORMAL)
                .eq(Comment::getRootId, COMMENT_ROOT);       //首先查出根评论
        Page<Comment> commentPage = new Page(commentParam.getPageNum(), commentParam.getPageSize());
        Page<Comment> page = page(commentPage, queryWrapper);
        List<Comment> comments = page.getRecords();*/
//       修改limit起始位置为*（页码数-1）*size
        commentParam.setPageNum((commentParam.getPageNum() - 1) * commentParam.getPageSize());
        List<Comment> comments = baseMapper.getCommentList(commentParam);
        Long total = baseMapper.getCommentTotal(commentParam);
//        封装为Vo对象
        List<CommentVo> commentVos = toCommentVoList(comments);
        for (CommentVo commentVo : commentVos) {
//            填充子评论
            commentVo.setChildren(getChildren(commentVo.getId()));
        }
//        这里封装的total是根评论的数目
        PageVo pageVo = new PageVo(commentVos, total);
        return ResponseResult.okResult(pageVo);
    }

    //    发表评论
    @Override
    public ResponseResult addComment(Comment comment) {
//       后端判断：评论内容不能为空
        if (!StringUtils.hasText(comment.getContent())) {
            throw new SystemException(AppHttpCodeEnum.CONTENT_NOT_NULL);
        }
        save(comment);
        return ResponseResult.okResult();
    }



    //  这里只查询二级评论，其实b站的评论列表也是最多到二级评论
    private List<CommentVo> getChildren(Long rootId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootId, rootId)
                .orderByAsc(Comment::getCreateTime);
        List<Comment> comments = list(queryWrapper);
        List<CommentVo> commentVos = toCommentVoList(comments);
        return commentVos;
    }

    //    该方法在其他成员方法内部调用，所以私有化
    private List<CommentVo> toCommentVoList(List<Comment> list) {

        List<CommentVo> commentVos = BeanCopyUtils.copyBeanList(list, CommentVo.class);
//        补充属性，toCommentUserName，username，children
        for (CommentVo commentVo : commentVos) {
//                补充username属性
            commentVo.setUsername(userService.getById(commentVo.getCreateBy()).getNickName());
//                补充toCommentUserName属性
            if (!COMMENT_ROOT.equals(commentVo.getToCommentUserId().toString())) {     //不为根评论
                commentVo.setToCommentUserName(userService.getById(commentVo.getToCommentUserId()).getNickName());
            }
        }
        return commentVos;
    }
}

