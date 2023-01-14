package com.cm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cm.domain.dto.TagListDto;
import com.cm.domain.entity.SystemException;
import com.cm.domain.entity.Tag;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.domain.vo.PageVo;
import com.cm.domain.vo.ResponseResult;
import com.cm.domain.vo.TagVo;
import com.cm.mapper.TagMapper;
import com.cm.service.TagService;
import com.cm.utils.BeanCopyUtils;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 标签(Tag)表服务实现类
 *
 * @author makejava
 * @since 2023-01-11 21:53:40
 */
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Override
    public ResponseResult pageTagList(Integer pageNum, Integer pageNum1, TagListDto tagListDto) {

        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        String name = tagListDto.getName();
        String remark = tagListDto.getRemark();
//        根据搜索关键字进行匹配：不过这里好像不是模糊查询
        queryWrapper.like(StringUtils.hasText(name), Tag::getName, name);
        queryWrapper.like(StringUtils.hasText(remark), Tag::getRemark, remark);
        queryWrapper.orderByAsc(Tag::getId);
        Page<Tag> tagPage = new Page<>();
        Page<Tag> page = page(tagPage, queryWrapper);
//        获取分页查询结果
        List<Tag> records = page.getRecords();
        long total = page.getTotal();
        List<TagVo> tagVoList = toTagVoList(records);
        PageVo pageVo = new PageVo(tagVoList, total);
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult addTag(TagListDto tagListDto) {
        String name = tagListDto.getName();
        String remark = tagListDto.getRemark();
        if (name == null || remark == null) {
            throw new SystemException(AppHttpCodeEnum.KEYWORDS_NOT_NULL);
        }
        Tag tag = new Tag();
        tag.setName(name);
        tag.setRemark(remark);
        save(tag);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateTagById(TagListDto tagListDto) {
        Tag tag = getById(tagListDto.getId());
        if (tag.getName().equals(tagListDto.getName()) && tag.getRemark().equals(tagListDto.getRemark())) {
//            如果都相同就没必要去操作数据库
            return ResponseResult.okResult();
        }
        tag.setName(tagListDto.getName());
        tag.setRemark(tagListDto.getRemark());
        if (updateById(tag))
            return ResponseResult.okResult();
        return ResponseResult.errorResult(556, "更新标签失败");
    }

    @Override
    public ResponseResult getTagById(Long id) {
        Tag tag = getById(id);
        TagVo tagVo = BeanCopyUtils.copyBean(tag, TagVo.class);
        return ResponseResult.okResult(tagVo);
    }

    @Override
    public ResponseResult deleteTags(List<Long> ids) {
        boolean deleted = false;
        if (ids.size() == 1) {
//            只删除一个标签
            deleted = removeById(ids.get(0));
        }
//        批量删除
        deleted = removeByIds(ids);
        if (deleted)
            return ResponseResult.okResult();
        else
            return ResponseResult.errorResult(556, "删除标签失败");
    }

    @Override
    public ResponseResult listAllTags() {
        List<Tag> tagList = list();
        List<TagVo> tagVoList = BeanCopyUtils.copyBeanList(tagList, TagVo.class);
        return ResponseResult.okResult(tagVoList);
    }


    public List<TagVo> toTagVoList(List<Tag> tagList) {
        return BeanCopyUtils.copyBeanList(tagList, TagVo.class);
    }
}

