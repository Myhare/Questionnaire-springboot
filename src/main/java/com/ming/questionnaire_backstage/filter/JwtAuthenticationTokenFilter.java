package com.ming.questionnaire_backstage.filter;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ming.questionnaire_backstage.mapper.UserMapper;
import com.ming.questionnaire_backstage.pojo.LoginUser;
import com.ming.questionnaire_backstage.pojo.User;
import com.ming.questionnaire_backstage.utils.JwtUtil;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取token
        String token = request.getHeader("token");
        // System.out.println(StringUtils.hasText(token));
        // 如果没有token，说明用户第一次登录
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
        // TODO 需要修改---通过userid查询数据库中的user信息，临时的权限验证
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        User user = userMapper.selectOne(queryWrapper);
        // 获取对应用户的权限信息
        List<String> powerList = userMapper.selectPowerById(userId);
        LoginUser loginUser = new LoginUser(user, powerList);

        // TODO 暂时不使用redis获取数据，以后改进
        // 通过userId在redis中查询是否登录
        // String redisKey = "login:"+userId;
        // LoginUser loginUser = redisCache.getCacheObject(redisKey);
        // if (Objects.isNull(loginUser)){
        //     throw new RuntimeException("用户未登录");
        // }
        // 存入SecurityContextHolder，用来在后面的过滤器中认证使用
        // 获取权限信息，封装到Authentication中
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser,null,loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // 放行过滤器
        filterChain.doFilter(request,response);
    }

}
