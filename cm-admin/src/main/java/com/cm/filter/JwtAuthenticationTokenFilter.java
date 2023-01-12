package com.cm.filter;

import com.alibaba.fastjson.JSON;
import com.cm.domain.entity.LoginUser;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.domain.vo.ResponseResult;
import com.cm.utils.JwtUtil;
import com.cm.utils.RedisCache;
import com.cm.utils.WebUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

//继承抽象类OncePerRequestFilter而不实现Filter接口，防止过滤器被重复走
// （servlet2.4后通过forward转发的页面不会被过滤，可能被重复走）
//OncePerRequestFilter：只走一次，在请求前执行
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

//        1.获取token
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
//            放行，让后面的过滤器执行：之后的过滤器会检查出未登录
            filterChain.doFilter(request, response);
            return; //防止执行完后倒过来执行过滤器再运行下面的代码
        }
//        2.解析token
        String userId;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = claims.getSubject();
        } catch (Exception e) {
//            token超时或者token非法  :如果token过期会无法解析
//              过滤器这里抛出异常不会被全局异常处理器接收,所以要直接返回给controller
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
            WebUtils.renderString(response, JSON.toJSONString(result));     //返回给前端
            return;
        }
//        3.由userId从redis中获取用户信息
        String key = "AdminLogin:" + userId;
        LoginUser loginUser = redisCache.getCacheObject(key);   //这里定义的是泛型方法，可以直接获取LoginUser
        if (Objects.isNull(loginUser)) {
            // 获取值为空
//            throw new RuntimeException("当前用户未登录！");
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
            WebUtils.renderString(response, JSON.toJSONString(result));     //返回给前端
            return;
        }
//        4.封装Authentication(调用三参数方法，会设置该用户已认证)  ：这里封装的是完整体的usernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(loginUser, null, null);

//        5.存入SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

//         之后再放行
        filterChain.doFilter(request, response);
    }
}
