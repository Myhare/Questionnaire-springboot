package com.ming.questionnaire_backstage.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)  // 忽略类中不存在的字段，防止反序列化的时候报错
public class LoginUser implements UserDetails { // 自定义一个响应类实现UserDetails接口，SpringSecurity验证时使用
    private User user;
    // 登录用户信息
    // 权限信息列表
    private List<String> permissions;

    public LoginUser(User user) {
        this.user = user;
        this.permissions = null;
    }

    public LoginUser(User user,List<String> permissions) {
        this.user = user;
        this.permissions = permissions;
    }

    // 在存入redis中时候，为了安全考虑，redis不会允许存储，这里忽略,之后只需要将permissions存入redis也可以查询到权限
    // @JSONField(serialize = false)  // fastJSON添加注解后这个属性就不会被序列化到流当中
    @JsonIgnore   // jackJSON忽略这个字段序列化，不然不能反序列化
    private List<SimpleGrantedAuthority> authorities;

    // 返回权限信息
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 将我们自定义的permission中String类型的权限信息，封装成SimpleGrantedAuthority对象

        // 为了防止每次授权的时候都重复下面的代码，只有第一次才添加，避免不必要的性能浪费
        if (authorities != null){
            return authorities;
        }

        authorities = new ArrayList<SimpleGrantedAuthority>(); // 赋初始值，不然会报空指针异常
        // 传统方式
        // 将权限信息封装成一个GrantedAuthority列表
        for (String permission : permissions) {
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(permission);
            authorities.add(authority);
        }


        // 函数式编程方法返回权限
        // authorities = permissions.stream()
        //         .map(SimpleGrantedAuthority::new)
        //         .collect(Collectors.toList());

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getUserPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    // 判断是否没过期
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 是否没有超时
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 是否可用
    @Override
    public boolean isEnabled() {
        return true;
    }
}
