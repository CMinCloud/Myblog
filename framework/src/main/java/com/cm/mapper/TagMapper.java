package com.cm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cm.domain.entity.Tag;
import org.apache.ibatis.annotations.Mapper;


/**
 * 标签(Tag)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-11 21:53:40
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

}

