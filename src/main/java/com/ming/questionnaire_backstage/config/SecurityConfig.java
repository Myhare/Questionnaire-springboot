package com.ming.questionnaire_backstage.config;

import com.ming.questionnaire_backstage.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)  // 开启权限认证
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // 导入自定义认证失败处理类
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    // 认证过滤器
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    // 创建一个BCryptPasswordEncoding注入容器,设置密码验证方法
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(); // 密码验证默认使用BCryptPassword加密
    }

    // 重写方法使得AuthenticationManager暴露出去,登录验证需要使用
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // 重写configure方法设置放行路径
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 关闭csrf
                .csrf().disable()
                // 不通过session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 允许使用RequestMatcher实现基于HttpServletRequest限制访问（即通过 URL 模式）。
                .authorizeRequests()
                // 对于登录接口，所有用户都可以访问
                .antMatchers("/login").permitAll()  // 允许所有用户访问这个接口
                // 允许所有用户访问后台登录接口
                .antMatchers("/admin/login").permitAll()
                // 允许所有用户进行用户注册
                .antMatchers("/user/register/**").permitAll()  // 所有用户都允许访问
                // 允许用户获取头像
                .antMatchers("/uploadFile/**").anonymous()  // 前台直接从img中获取头像，没有携带token
                // 允许匿名用户访问绑定邮箱接口
                .antMatchers("/user/bandEmail").anonymous()
                // 除了上面定义的所有请求都需要鉴权认证
                .anyRequest().authenticated();

        // 自定义认证失败处理
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)  // 认证异常处理
                .accessDeniedHandler(accessDeniedHandler);   // 授权异常处理

        // 添加自定义过滤器,添加在UsernamePasswordAuthenticationFilter过滤器之前
        http.addFilterBefore(jwtAuthenticationTokenFilter,UsernamePasswordAuthenticationFilter.class);
    }
}
