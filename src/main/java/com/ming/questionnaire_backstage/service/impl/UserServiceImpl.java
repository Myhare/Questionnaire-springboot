package com.ming.questionnaire_backstage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ming.questionnaire_backstage.mapper.UserMapper;
import com.ming.questionnaire_backstage.mapper.UserRoleMapper;
import com.ming.questionnaire_backstage.pojo.LoginUser;
import com.ming.questionnaire_backstage.pojo.ResponseResult;
import com.ming.questionnaire_backstage.pojo.User;
import com.ming.questionnaire_backstage.pojo.UserRole;
import com.ming.questionnaire_backstage.service.UserService;
import com.ming.questionnaire_backstage.utils.AsyncUtils;
import com.ming.questionnaire_backstage.utils.JwtUtil;
import com.ming.questionnaire_backstage.utils.RedisUtil;
import com.ming.questionnaire_backstage.utils.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sun.rmi.runtime.Log;

import javax.servlet.ServletContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

@Service
@Transactional  // 开启事务管理
public class UserServiceImpl implements UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RedisUtil redisUtil;


    @Value("${web.get-head-path}")
    private String getHeadPath;    // 获取头像的路径
    @Value("${web.bandEmailHost}")
    private String bandEmailHost;

    @Autowired
    JavaMailSenderImpl mailSender;  // 发送邮件工具
    @Value("${spring.mail.username}")
    private String formName;  // 发送邮件的邮箱

    // 登录用户
    @Override
    public ResponseResult login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(user.getUserName(), user.getUserPassword());

        // 通过authenticationManager.authenticate进行用户验证 需要传入一个authentication接口参数，这里我们创建一个authentication的UsernamePasswordAuthenticationToken实现类
        // authenticationManager会自动调用UserDetailsService进行用户的验证，因为我们之前自定义了UserDetailsService，所以会调用我们自定义的UserDetailsService进行验证
        // UserDetailsService返回一个LoginUser对象，会自动封装到authenticate中的principal属性中,如果认证没通过，直接报错
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        // 如果认证没通过，给出对应提示
        if (authenticate == null) {  // 说明认证没有通过，抛出异常让全局过滤器捕获
            throw new RuntimeException("登录失败,用户名或密码错误");
        }
        // 如果认证通过了,生成一个token传给前端
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getUserId();
        String token = JwtUtil.createJWT(userId);

        // 通过userid查询用户详细信息传入前端
        User userInfo = userMapper.selectById(userId);
        // 更新用户头像路径
        if (userInfo.getUserHeadPath()!=null){
            userInfo.setUserHeadPath(getHeadPath+userInfo.getUserHeadPath());
        }
        // 将完整的用户信息存入到redis中
        redisUtil.set("login:"+userId,loginUser,60*60*3);  // 设置三个小时的过期时间
        // 在redis中存入今天登录的时间
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String loginUserCountKey = sdf.format(date) + ":userCount";
        redisUtil.incr(loginUserCountKey,1);  // 今天登录用户次数加一
        Map<String, Object> map = new HashMap<>();
        map.put("token",token);
        map.put("userInfo",userInfo);
        return new ResponseResult(200,"登录成功",map);
    }

    // 用户登出
    @Override
    public ResponseResult logout(String userId) {
        String redisKey = "login:" + userId;
        redisUtil.del(redisKey);
        return new ResponseResult(200,"登出成功");
    }

    // 通过id获取用户详细信息
    @Override
    public User getUserInfoById(String userId) {
        User user = userMapper.selectUserInfo(userId);
        // 更新用户头像路径
        if (user.getUserHeadPath()!=null){
            user.setUserHeadPath(getHeadPath+user.getUserHeadPath());
        }
        return user;
    }

    // 判断一个用户名是否在数据库中存在
    @Override
    public int usernameIsExit(String userName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userName);
        return userMapper.selectCount(queryWrapper);  // 将查询到的数量返回出去
    }

    // 添加一个普通用户
    @Override
    public int addUser(User user) {
        // 插叙user中的id是否在数据库中已经存在，如果存在，报错
        if (usernameIsExit(user.getUserName())>0){
            throw new RuntimeException("数据出错，用户id已经存在");
        }
        // 用户注册，默认将用户昵称设置为用户名
        user.setUserId(user.getUserName());
        user.setState(1);  // 设置状态为正常，不然添加的时候因为java默认值是0，添加到数据库会显示被封禁
        // 将密码进行加密存储
        // SpringSecurity底层中使用的是下面的加密方法
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));   // 密码加密存储
        // 默认添加的用户都是普通用户
        // TODO 这里普通用户在数据库中是写死的，以后优化
        int i = userRoleMapper.insert(new UserRole(user.getUserId(), "db335e541e7e40dca964593c64248164"));// 默认角色id都是普通用户
        int j = userMapper.insert(user);
        return i*j;  // 上面两个数据库操作，只要有一个失败，就返回错误
    }

    // 通过用户id获取头像存储位置
    @Override
    public String getUserHeadById(String userId) {
        User user = userMapper.selectById(userId);
        if (user!=null){
            return user.getUserHeadPath();
        }else {
            throw new RuntimeException("用户名格式错误");
        }
    }

    // 通过用户id修改用户昵称
    @Override
    public int updateUserNameById(String userId, String userName) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("user_id",userId)
                .set("user_name",userName);
        return userMapper.update(null, updateWrapper);
    }

    // 通过id修改用户简介
    @Override
    public int updateUserIntroduceById(String userId, String userIntroduce) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("user_id",userId)
                .set("user_introduce",userIntroduce);
        return userMapper.update(null,updateWrapper);
    }

    // 检测用户输入的旧密码是否正确
    @Override
    public boolean checkOldPassword(String userId, String oldPassword) {
        User user = userMapper.selectById(userId);
        // 创建一个BCryptPasswordEncoder对象，用来对数据库中加密的数据进行解密，并且判断是否相等
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(oldPassword, user.getUserPassword());
    }

    // 通过id修改用户密码
    @Override
    public int updatePasswordById(String userId, String newPassword) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        // 对密码进行加密存储
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodePwd = passwordEncoder.encode(newPassword);
        updateWrapper
                .eq("user_id",userId)
                .set("user_password",encodePwd);
        return userMapper.update(null,updateWrapper);
    }

    // 发送随机的六位数验证码给邮箱，返回验证码
    @Override
    public ResponseResult sendEmailCode(String email) {
        // 对邮件的格式进行验证
        Pattern pattern = Pattern.compile("^([A-z0-9]{6,18})(\\w|\\-)+@[A-z0-9]+\\.([A-z]{2,3})$");
        if (!pattern.matcher(email).matches()){  // 如果不满足正则表达式，返回0
            return new ResponseResult(401,"邮箱格式错误");
        }
        // 获取当前登录的用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        // 判断用户输入的email和数据库中搞得email是否相同
        String userEmail = loginUser.getUser().getUserEmail();
        if (!userEmail.equals("null") && userEmail.equals(email)){
            return new ResponseResult(401,"已经绑定此邮箱");
        }
        String redisEmail = (String) redisUtil.get(email + ":key");
        // 判断servletContext中有没有对应的内容，如果有，提醒用户不要重复发布邮件
        if (redisEmail!=null){
            return new ResponseResult(401,"请不要重复发送邮件");
        }

        // 发送邮件功能
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        //生成验证地址
        String charValue = UUIDUtils.getUUID();

        String userId = loginUser.getUser().getUserId();

        mailMessage.setSubject("注册验证码");
        mailMessage.setFrom(formName);
        mailMessage.setTo(email);
        mailMessage.setText("你好，请前往"+bandEmailHost+email+"/"+userId+"/"+charValue+" 进行验证");  // 向对应的邮箱中发送确定绑定邮箱的邮件，内容是绑定邮箱的地址

        // 发送邮件到指定邮件
        mailSender.send(mailMessage);

        // 将邮箱验证码存储到redis中,设置3分钟的有效时间
        redisUtil.set(email+":key",charValue,60*3);
        return new ResponseResult(200,"发送成功，请前往邮箱验证");  // 发送成功
    }

    // 更改绑定邮箱
    @Override
    public ResponseResult bandEmail(String email, String key,String userId) {
        // 对邮件的格式进行验证
        Pattern pattern = Pattern.compile("^([A-z0-9]{6,18})(\\w|\\-)+@[A-z0-9]+\\.([A-z]{2,3})$");
        if (!pattern.matcher(email).matches()){  // 如果不满足正则表达式，返回0
            return new ResponseResult(401,"邮箱格式错误");
        }
        // 如果email为null或者和servletContext中的内容不相同，返回2，说明有问题
        String emailKey = (String) redisUtil.get(email + ":key");
        if (emailKey==null || !emailKey.equals(key)){
            return new ResponseResult(401,"验证码过期，请重新绑定");
        }
        // 到这里说明验证成功，将email存入对应的数据库中
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("user_id",userId)
                .set("user_email",email);
        int i = userMapper.update(null, updateWrapper);
        if (i>0){   // 如果绑定邮箱变更了
            redisUtil.del(email + ":key");   // 删除redis中对应的key
            return new ResponseResult(200,"邮箱绑定成功");
        }else {
            return new ResponseResult(401,"发生错误，邮箱绑定失败");
        }
    }


    // 通过用户id查询用户邮箱
    @Override
    public String getEmailById(String userId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .select("user_email")
                .eq("user_id",userId);
        User user = userMapper.selectOne(queryWrapper);
        return user.getUserEmail();
    }
}
