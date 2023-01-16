package com.cm.handler.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.cm.domain.entity.SystemException;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
//Mybatis属性自动注入
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Long userId = null;
        try {
        userId = SecurityUtils.getUserId();
//            这里需要进行登录验证判定，如果不登录无法获取userId
        } catch (Exception e) {
            throw new SystemException(AppHttpCodeEnum.NEED_LOGIN);
        }
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("createBy", userId, metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateBy", userId, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName(" ", SecurityUtils.getUserId(), metaObject);
    }
}