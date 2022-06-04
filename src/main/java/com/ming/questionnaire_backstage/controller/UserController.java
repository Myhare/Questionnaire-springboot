package com.ming.questionnaire_backstage.controller;

import com.ming.questionnaire_backstage.pojo.LoginUser;
import com.ming.questionnaire_backstage.pojo.ResponseResult;
import com.ming.questionnaire_backstage.pojo.User;
import com.ming.questionnaire_backstage.service.AsyncService;
import com.ming.questionnaire_backstage.service.FileUploadService;
import com.ming.questionnaire_backstage.service.UserService;
import com.ming.questionnaire_backstage.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@RestController
@RequestMapping("/user")
public class UserController {

    @Value("${web.get-head-path}")
    private String getHeadPath;    // 获取头像的路径

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private UserService userService;

    // 上传用户头像
    @PostMapping("/uploadHeader")
    public ResponseResult uploadHeader(@RequestParam MultipartFile avatar){
        return fileUploadService.uploadHeader(avatar);
    }

    // 判断一个用户名是否存在
    @GetMapping("/register/usernameIsExit/{username}")
    public ResponseResult usernameIsExit(@PathVariable(value = "username") String userName){
        int i = userService.usernameIsExit(userName);
        return new ResponseResult(200,"查询成功",i);
    }

    // 注册用户
    @PostMapping("/register/registerUser")
    public ResponseResult registerUser(@RequestBody User user){
        int i = userService.addUser(user);
        if (i > 0){
            return new ResponseResult(200,"注册成功!");
        }else {
            return new ResponseResult(500,"出现错误，注册用户失败,请稍后再试");
        }
    }

    // 获取登录用户头像
    @GetMapping("/getUserHead")
    public ResponseResult getUserHead(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String userId = loginUser.getUser().getUserId();
        if (StringUtils.isEmpty(userId)){
            return new ResponseResult(500,"用户名错误");
        }else {
            return new ResponseResult(200,"查询头像成功",loginUser.getUser().getUserHeadPath());
        }
    }

    // 修改用户昵称
    @PostMapping("/updateUserName")
    public ResponseResult updateUserName(@RequestBody Map<String,Object> map){
        String userId = (String) map.get("userId");
        String userName = (String) map.get("userName");
        // 通过用户id修改用户昵称
        int i = userService.updateUserNameById(userId, userName);
        if (i>0){
            return new ResponseResult(200,"修改昵称成功",userName);
        }else {
            return new ResponseResult(401,"修改用户昵称失败，请稍后再试");
        }
    }

    // 修改用户简介
    @PostMapping("/updateUserIntroduce")
    public ResponseResult updateUserIntroduce(@RequestBody Map<String,Object> map){
        String userId = (String) map.get("userId");
        String userIntroduce = (String) map.get("userIntroduce");
        // 通过用户id修改用户昵称
        int i = userService.updateUserIntroduceById(userId, userIntroduce);
        if (i>0){
            return new ResponseResult(200,"修改简介成功",userIntroduce);
        }else {
            return new ResponseResult(401,"修改用户简介失败，请稍后再试");
        }
    }

    // 判断用户输入的旧密码是否正确
    @GetMapping("/checkOldPassword")
    public ResponseResult checkOldPassword(@Param(value = "userId")String userId,@Param(value = "oldPassword")String oldPassword){
        boolean flag = userService.checkOldPassword(userId, oldPassword);
        if (flag){
            return new ResponseResult(200,"ok");
        }else {
            return new ResponseResult(401,"旧密码输入错误");
        }
    }

    // 修改用户密码
    @PostMapping("/doUpdatePassword")
    public ResponseResult doUpdatePassword(@RequestBody Map<String,String> map){
        String userId = map.get("userId");
        String newPassword = map.get("newPassword");
        int i = userService.updatePasswordById(userId, newPassword);
        if (i>0){
            return new ResponseResult(200,"修改密码成功");
        }else {
            return new ResponseResult(401,"修改密码失败，请稍后再试");
        }
    }

    // 发送验证邮箱
    @PostMapping("/sendEmail")
    public ResponseResult sendEmail(@RequestBody String email){
        // String email = map.get("email");
        // 发送邮件到对应的邮箱中
        return userService.sendEmailCode(email);
    }

    // 获取登录用户详细信息
    @GetMapping("/getUserInfo")
    public ResponseResult getUserInfo(HttpServletRequest request){
        // 获取当前登录用户信息
        String token = request.getHeader("token");
        // 解析token
        String userId;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("token非法");
        }
        User user = userService.getUserInfoById(userId);
        if (user==null){
            return new ResponseResult(401,"查询失败，用户不存在");
        }else {
            return new ResponseResult(200,"查询成功",user);
        }
    }

    // 用户验证邮箱
    @GetMapping("/bandEmail")
    public ResponseResult bandEmail(@Param("email")String email, @Param("key")String key,@Param("userId")String userId){
        // System.out.println(email);
        // System.out.println(key);
        // System.out.println(userId);
        return userService.bandEmail(email, key, userId);
    }

}
