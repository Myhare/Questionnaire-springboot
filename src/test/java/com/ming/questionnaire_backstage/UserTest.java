package com.ming.questionnaire_backstage;

import com.ming.questionnaire_backstage.mapper.UserMapper;
import com.ming.questionnaire_backstage.pojo.User;
import com.ming.questionnaire_backstage.service.UserService;
import com.ming.questionnaire_backstage.utils.UUIDUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class UserTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    // 测试添加用户
    @Test
    void myInsert(){
        int i = userMapper.insert(
                new User(
                        UUIDUtils.getUUID(),
                        "dede","123456","1940307627@@qq.com",
                        "",""));
        if (i>0){
            System.out.println("插入成功");
        }else {
            System.out.println("插入失败");
        }
    }

    // 测试通过userId查询权限
    @Test
    void selectPowerById(){
        List<String> list = userMapper.selectPowerById("dede");
        for (String s : list) {
            System.out.println(s);
        }
    }

    // 测试修改用户头像名
    @Test
    void testUpdateHeaderById(){
        int i = userMapper.updateHeaderById("5b2825bed36a458dae8a5031475e732e", "dede");
        if (i>0){
            System.out.println("设置成功");
        }else {
            System.out.println("设置失败");
        }
    }

    // 测试通过id查询user信息
    @Test
    void testSelectUserById(){
        User userInfo = userService.getUserInfoById("5b2825bed36a458dae8a5031475e732e");
        System.out.println(userInfo);
    }

    // 查询用户名是否存在
    @Test
    void testIsExitUsername(){
        int i = userService.usernameIsExit("dede");
        if (i>0){
            System.out.println("存在");
        }else {
            System.out.println("不存在");
        }
    }

    // 测试验证旧密码
    @Test
    void testCheckOldPwd(){
        boolean flag = userService.checkOldPassword("dede", "12346");
        if (flag){
            System.out.println("旧密码正确");
        }else {
            System.out.println("旧密码错误");
        }
    }

}
