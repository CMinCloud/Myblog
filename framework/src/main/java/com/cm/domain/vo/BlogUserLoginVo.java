package com.cm.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 该类作为ResponseResult的返回值返回给前端
 */
public class BlogUserLoginVo {
    private userInfoVo userInfo;   //注意前台接收名为userInfo
    private String token;
}
