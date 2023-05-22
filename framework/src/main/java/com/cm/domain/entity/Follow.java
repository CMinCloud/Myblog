package com.cm.domain.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * (Follow)表实体类
 *
 * @author makejava
 * @since 2023-02-25 23:06:44
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@TableName("cm_follow")
public class Follow  {
    
    private Long id;
    //用户id
    private Long userId;
    //被关注者id
    private Long followUserId;

}

