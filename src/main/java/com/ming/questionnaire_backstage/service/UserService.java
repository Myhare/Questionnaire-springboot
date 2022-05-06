package com.ming.questionnaire_backstage.service;

import com.ming.questionnaire_backstage.pojo.ResponseResult;
import com.ming.questionnaire_backstage.pojo.User;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {

    // 用户登录
    ResponseResult login(User user);

    // 用户登出
    ResponseResult logout(String userId);

    // 获取登录用户详细信息
    User getUserInfoById(String userId);

    // 查询数据库中是否有用户名
    int usernameIsExit(String userName);

    // 添加一个用户
    int addUser(User user);

    // 通过用户id获取用户头像存储位置
    String getUserHeadById(String userId);

    // 通过用户id修改用户昵称
    int updateUserNameById(String userId,String userName);

    // 通过用户id修改用户简介
    int updateUserIntroduceById(String userId,String userIntroduce);

    // 查询用户输入的旧密码是否正确
    boolean checkOldPassword(String userId,String oldPassword);

    // 修改用户的密码
    int updatePasswordById(String userId,String newPassword);

    // 发送邮箱验证码
    ResponseResult sendEmailCode(String email);

    // 验证邮箱
    ResponseResult bandEmail(String email,String key,String userId);



}
