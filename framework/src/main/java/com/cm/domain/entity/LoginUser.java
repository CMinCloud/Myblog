package com.cm.domain.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class LoginUser implements UserDetails {

    private User user;

    //    存储用户权限信息
    private List<String> permissions;

    //    存储用户角色
    private List<String> roles;


    //    使用构造器注入的方式 引入依赖
    public LoginUser(User user, List<String> permissions) {
        this.user = user;
        this.permissions = permissions;
    }

    public LoginUser(User user, List<String> permissions, List<String> roles) {
        this.user = user;
        this.permissions = permissions;
        this.roles = roles;
    }

    //    获取用户权限的接口类型
    @JSONField(serialize = false)
    private List<SimpleGrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //        已经获取过用户权限，则直接返回
        if (authorities != null) {
            return authorities;
        }
        if (this.permissions == null) {     //对于普通用户，没有封装权限
            return null;
        }
        authorities = new ArrayList<>();
//        封装用户权限为SimpleGrantedAuthority
        authorities = permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
