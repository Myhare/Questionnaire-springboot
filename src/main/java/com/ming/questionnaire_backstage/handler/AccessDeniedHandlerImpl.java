package com.ming.questionnaire_backstage.handler;

import com.alibaba.fastjson.JSON;

import com.ming.questionnaire_backstage.pojo.ResponseResult;
import com.ming.questionnaire_backstage.utils.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    // 授权失败自定义实现类
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        ResponseResult responseResult = new ResponseResult(HttpStatus.FORBIDDEN.value(),e.getMessage());
        String json = JSON.toJSONString(responseResult);  // 将返回值对象转化为JSON对象
        // 处理异常
        WebUtils.renderString(response,json);
    }
}
