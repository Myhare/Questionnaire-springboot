package com.ming.questionnaire_backstage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ming.questionnaire_backstage.mapper.UserMapper;
import com.ming.questionnaire_backstage.pojo.LoginUser;
import com.ming.questionnaire_backstage.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    // 继承UserDetailsService之后会在登录的时候自动执行
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 通过传入的username查询用户在数据库中的信息
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",username);  // 设置查询条件
        User user = userMapper.selectOne(queryWrapper);
        if (user==null){
            throw new RuntimeException("用户名或密码错误");
        }
        // 查询用户权限信息
        List<String> powerList = userMapper.selectPowerById(user.getUserId());
        return new LoginUser(user,powerList);  // 创建一个LoginUser,封装user对象和对应的权限信息进去
    }
}
