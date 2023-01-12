package com.cm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cm.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 用户表(User)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-03 12:12:26
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    List<String> getAdminPermissions();

    List<String> getPermissions(Long id);

    List<String> getSystemRoles(Long id);
}

