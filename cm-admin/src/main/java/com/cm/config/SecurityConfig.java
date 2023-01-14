package com.cm.config;

import com.cm.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@Configuration
@EnableWebSecurity      // 该注解中包含Configuration和EnableGlobalAuthentication
@EnableGlobalMethodSecurity(prePostEnabled = true)      //使用该注解后开启Security的注解使用
public class SecurityConfig {


    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    //    使用autowired注入依赖，并将整合到security当中
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //关闭csrf
                .csrf().disable()
                //不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 对于登录接口 允许匿名访问
                .antMatchers("/user/login").anonymous()
//                .antMatchers("/logout").authenticated()     //认证之后才能访问
//                .antMatchers("/user/userInfo").authenticated()
                // 除上面外的所有请求全部不需要认证即可访问
//                添加权限控制
//                .antMatchers("/content/category/export").hasAuthority("content:category:export")
                .anyRequest().authenticated();

//        引入security的自定义异常
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

//        在用户登录过滤器之前进行过滤校验
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        http.logout().disable();        //关闭默认注销功能，否则自定义的logout可能退出失败
        //允许跨域
        http.cors();
        return http.build();
    }

    //    一定要返回一个密码配置类
    @Bean
    public PasswordEncoder passwordEncoder() {
        //返回一个对应的 实现类,来解析加密 字段
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    //    实现AuthenticationManager接口
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();   //返回身份认证管理器
    }
}
