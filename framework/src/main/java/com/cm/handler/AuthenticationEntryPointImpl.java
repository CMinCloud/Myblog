package com.cm.handler;


import com.alibaba.fastjson.JSON;
import com.cm.domain.vo.ResponseResult;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.utils.WebUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 自定义登录认证异常的处理类
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {


    /**
     * commence：开始
     *
     * @param request
     * @param response
     * @param authException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

//        根据异常类型判断  是 登录失败(账号密码错误)  还是 认证失败(过滤器token认证) 并做不同的响应

//        封装异常信息为自定义异常类
        ResponseResult result = null;
        if (authException instanceof BadCredentialsException) {
             result = ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_ERROR);
        }else if(authException instanceof InsufficientAuthenticationException){
             result = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }else {
            result =  ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR,"认证或授权失败");
        }

//        将异常消息封装为JSON
        String jsonString = JSON.toJSONString(result);
        //        调用工具类返回值给前端
        WebUtils.renderString(response, jsonString);
    }
}
