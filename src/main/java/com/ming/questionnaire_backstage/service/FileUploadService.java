package com.ming.questionnaire_backstage.service;

import com.ming.questionnaire_backstage.pojo.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

// 文件上传服务接口
public interface FileUploadService {

    ResponseResult uploadHeader(MultipartFile multipartFile);

}
