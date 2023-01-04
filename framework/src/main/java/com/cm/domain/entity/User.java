package com.cm.domain.entity;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 用户表(User)表实体类
 *
 * @author makejava
 * @since 2023-01-03 12:12:19
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@TableName("sys_user")
public class User  {
    //主键
    private Long id;
    //用户名
    private String userName;
    //昵称
    private String nickName;
    //密码
    private String password;
    //用户类型：0代表普通用户，1代表管理员
    private String type;
    //账号状态（0正常 1停用）
    private String status;
    //邮箱
    private String email;
    //手机号
    private String phonenumber;
    //用户性别（0男，1女，2未知）
    private String sex;
    //头像
    private String avatar;
    //创建人的用户id
    private Long createBy;
    //创建时间
    private Date createTime;
    //更新人
    private Long updateBy;
    //更新时间
    private Date updateTime;
    //删除标志（0代表未删除，1代表已删除）
    private Integer delFlag;

}

