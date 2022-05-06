package com.ming.questionnaire_backstage;

import com.alibaba.fastjson.JSON;
import com.ming.questionnaire_backstage.mapper.PowerMapper;
import com.ming.questionnaire_backstage.mapper.RoleMapper;
import com.ming.questionnaire_backstage.mapper.UserMapper;
import com.ming.questionnaire_backstage.pojo.LoginUser;
import com.ming.questionnaire_backstage.pojo.Power;
import com.ming.questionnaire_backstage.pojo.Role;
import com.ming.questionnaire_backstage.pojo.User;
import com.ming.questionnaire_backstage.utils.JwtUtil;
import com.ming.questionnaire_backstage.utils.RedisUtil;
import com.ming.questionnaire_backstage.utils.UUIDUtils;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@SpringBootTest
class QuestionnaireBackstageApplicationTests {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PowerMapper powerMapper;

    // 发送邮件工具
    @Autowired
    JavaMailSenderImpl mailSender;


    @Autowired
    RedisTemplate redisTemplate;
    // redis工具类
    @Autowired
    RedisUtil redisUtil;

    @Test
    void contextLoads() {
        int a=33;
        int b=67;
        //BigDecimal 格式化工具    保留两位小数
        NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMaximumFractionDigits(2);

        //除法结果保留4位小数，
        double per = new BigDecimal((float)a/b).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();

        //格式化为百分比字符串（自带百分号）
        String Ratio = percent.format(per);
        System.out.println(Ratio);
    }


    // 测试密码加密
    @Test
    void testBCryptPasswordEncoder() {
        // SpringSecurity底层中使用的是下面的加密方法
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode1 = passwordEncoder.encode("123456");  // 传入密码的原文，返回加密后的字符串
        // String encode2 = passwordEncoder.encode("123456");
        System.out.println(encode1);
        // System.out.println(encode2);   // 每一次自动生成的对象都是不一样的
        // 校验密码
        // 校验密码的时候需要传入加密后的字符串（数据库中查找）,和用户输入的明文密码进行比较，如果一致返回true，否则返回false
        boolean matches = passwordEncoder.matches("123456", "$2a$10$HRmP/ugSgqXaTv7TzvylmuHsgqOytTAugEd7mdG5PHwMI0oJb1jh6");
        System.out.println(matches);
    }



    // 测试JWT
    @Test
    void testJWT(){
        String token = JwtUtil.createJWT("测试");
        System.out.println(token);
        Claims claims = null;
        try {
            claims = JwtUtil.parseJWT(token);
            String subject = claims.getSubject();
            System.out.println(subject);
            System.out.println(claims.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 测试添加角色
    @Test
    void testInsertRole(){
        Role role = new Role(UUIDUtils.getUUID(), "普通用户", 1);
        int i = roleMapper.insert(role);
        if (i>0){
            System.out.println("添加角色身份成功");
        }else {
            System.out.println("添加角色身份失败");
        }
    }

    // 测试添加权限
    @Test
    void testInsertPower(){
        Power power = new Power(UUIDUtils.getUUID(), "sys:user", 0);
        int i = powerMapper.insert(power);
        if (i>0){
            System.out.println("添加权限成功");
        }else {
            System.out.println("添加权限失败");
        }
    }

    // 测试发送邮件
    @Test
    void testSimpleEmail(){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("测试邮件");
        mailMessage.setText("死嘚");

        mailMessage.setFrom("1940307627@qq.com");
        mailMessage.setTo("545781591@qq.com");
        mailSender.send(mailMessage);
    }

    // 测试正则表达式校验
    @Test
    void testZh(){
        Pattern p1 = Pattern.compile("^([A-z0-9]{6,18})(\\w|\\-)+@[A-z0-9]+\\.([A-z]{2,3})$");
        boolean matches = p1.matcher("1232343@qq.com").matches();
        System.out.println(matches);
    }

    // 测试云端redis
    @Test
    void testRedis(){
        // Boolean userToday = redisTemplate.opsForValue().setBit("userToday", 0, true);
        // Set keys = redisTemplate.keys("*");
        // for (Object key : keys) {
        //     System.out.println(key);
        // }
        redisTemplate.opsForValue().set("k1","v1");

    }

    // 获取当前登录的时间并且格式化
    @Test
    void testDate(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(sdf.format(date));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE,-1);  // 时间减一
        Date data2 = calendar.getTime();
        System.out.println(sdf.format(data2));
    }

    // 测试云端redis
    @Test
    void testRedisUtil(){
        User user = new User(
                "1", "dede", "123",
                "123", "123", "345");
        LoginUser loginUser = new LoginUser(user,Arrays.asList("111","222"));
        boolean flag = redisUtil.set("testUser",loginUser);
        System.err.println(flag);
    }

    // 测试云端redis
    @Test
    void testRedisUtil2(){
        // Object testUser = redisUtil.get("testUser");
        // System.out.println(testUser);
        LoginUser loginUser = (LoginUser) redisUtil.get("testUser");
        System.out.println(loginUser);
    }

}
