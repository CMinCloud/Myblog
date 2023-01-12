package com.cm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cm.domain.entity.Tag;
import com.cm.mapper.TagMapper;
import com.cm.service.TagService;
import org.springframework.stereotype.Service;

/**
 * 标签(Tag)表服务实现类
 *
 * @author makejava
 * @since 2023-01-11 21:53:40
 */
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

}

