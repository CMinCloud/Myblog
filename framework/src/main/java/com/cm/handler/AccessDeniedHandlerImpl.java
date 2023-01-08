package com.cm.handler;

import com.alibaba.fastjson.JSON;
import com.cm.domain.vo.ResponseResult;
import com.cm.utils.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 自定义授权异常的处理类（没有权限访问）
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ResponseResult responseResult = new ResponseResult<>(HttpStatus.FORBIDDEN.value(), "您没有权限访问该请求！");
//        异常消息封装未json
        String jsonString = JSON.toJSONString(responseResult);
//        调用工具类返回值给前端
        WebUtils.renderString(response,jsonString);
    }
}
