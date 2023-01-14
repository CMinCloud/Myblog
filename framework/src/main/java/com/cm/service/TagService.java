package com.cm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cm.domain.dto.TagListDto;
import com.cm.domain.entity.Tag;
import com.cm.domain.vo.ResponseResult;

import java.util.List;

/**
 * 标签(Tag)表服务接口
 *
 * @author makejava
 * @since 2023-01-11 21:53:39
 */
public interface TagService extends IService<Tag> {
    ResponseResult pageTagList(Integer pageNum, Integer pageNum1, TagListDto tagListDto);

    ResponseResult addTag(TagListDto tagListDto);

    ResponseResult updateTagById(TagListDto tagListDto);

    ResponseResult getTagById(Long id);

    ResponseResult deleteTags(List<Long> ids);

    ResponseResult listAllTags();
}

