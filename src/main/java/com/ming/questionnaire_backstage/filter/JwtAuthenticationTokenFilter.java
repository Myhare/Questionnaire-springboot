package com.ming.questionnaire_backstage.filter;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ming.questionnaire_backstage.mapper.UserMapper;
import com.ming.questionnaire_backstage.pojo.LoginUser;
import com.ming.questionnaire_backstage.pojo.User;
import com.ming.questionnaire_backstage.utils.JwtUtil;
import com.ming.questionnaire_backstage.utils.RedisUtil;
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
import java.util.List;
import java.util.Objects;

// 这里继承Spring为我们创建的实现类
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 如果是登录接口和登出接口，直接放行
        if (request.getRequestURL().toString().contains("/login")){
            // 放行,后面还有其他的过滤器会处理没有token的问题
            filterChain.doFilter(request,response);
            return;
        }else if (request.getRequestURL().toString().contains("/logout2")){
            // 放行,后面还有其他的过滤器会处理没有token的问题
            filterChain.doFilter(request,response);
            return;
        }
        // 获取token
        String token = request.getHeader("token");
        // System.out.println(StringUtils.hasText(token));
        // 如果没有token，用户可能是在获取登录头像
        if (!StringUtils.hasText(token)){
            // 放行,后面还有其他的过滤器会处理没有token的问题
            filterChain.doFilter(request,response);
            return;
        }
        // 解析token
        String userId;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("token非法");
        }
        // 通过用户id查询用户权限
        String redisKey = "login:"+userId;
        LoginUser loginUser = (LoginUser) redisUtil.get(redisKey);

        if (Objects.isNull(loginUser)){
            throw new RuntimeException("登录信息过期，请重新登录");
        }
        // 存入SecurityContextHolder，用来在后面的过滤器中认证使用
        // 获取权限信息，封装到Authentication中
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser,null,loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // 放行过滤器
        filterChain.doFilter(request,response);
    }

}
