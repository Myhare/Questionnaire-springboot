package com.ming.questionnaire_backstage.service.impl;

import com.ming.questionnaire_backstage.mapper.UserMapper;
import com.ming.questionnaire_backstage.pojo.LoginUser;
import com.ming.questionnaire_backstage.pojo.ResponseResult;
import com.ming.questionnaire_backstage.service.FileUploadService;
import com.ming.questionnaire_backstage.utils.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private UserMapper userMapper;

    // 获取文件上传保存目录
    @Value("${web.upload-path}")
    private String uploadPath;

    // SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");

    // 用户上传头像
    @Override
    public ResponseResult uploadHeader(MultipartFile headerFile) {
        // System.out.println("FileUploadServiceImpl.uploadPath------>"+uploadPath);
        if (headerFile.isEmpty()) {
            throw new RuntimeException("图片不能为空");
        }
        if (headerFile.getSize() >= 10 * 1024 * 1024) {
            throw new RuntimeException("图片大小超出最大限制");
        }
        String newFileName = null;
        try {
            //获取文件名
            String oldFileName = headerFile.getOriginalFilename();
            //获取文件后缀名
            String suffixName = oldFileName.substring(oldFileName.lastIndexOf("."));
            // 自动生成文件名
            newFileName = UUIDUtils.getUUID()+suffixName;
            // 在 uploadPath 文件夹中通过日期对上传的文件归类保存
            // 比如：/2019/06/06/cf13891e-4b95-4000-81eb-b6d70ae44930.png
            // String format = sdf.format(new Date());
            String path = uploadPath+newFileName;  // 真实存储路径
            File dest = new File(path);
            File parentFile = dest.getParentFile();
            //检测是否存在该目录
            if (!dest.getParentFile().exists()){   // 如果不存在这个目录，新建一个目录
                dest.getParentFile().mkdirs();
            }
            //写入文件
            // headerFile.transferTo(dest);
            FileCopyUtils.copy(headerFile.getInputStream(), Files.newOutputStream(dest.toPath()));  // 因为上面方法使用还不知道怎么使用绝对路径，会自动添加一个相对路径，在linux系统下会报错，这里手动通过流来赋值文件


            // 将文件名字储存到user数据库中
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            String userId = loginUser.getUser().getUserId();
            // 通过id修改数据库中头像路径
            userMapper.updateHeaderById(userId,newFileName);
        } catch (IOException e) {
            System.out.println("头像上传出现问题");
            e.printStackTrace();
            throw new RuntimeException("上传头像出现问题");
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("newHeadPath","/uploadFile/"+newFileName);
        return new ResponseResult(200,"上传头像成功", map);
    }
}
