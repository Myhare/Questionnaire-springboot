package com.ming.questionnaire_backstage.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;

// 异步类
@Component
public class AsyncUtils {

    @Autowired
    private ServletContext servletContext;

    // 异步请求，延时删除对应的servletContext中的内容
    @Async
    public void delayRemoveCode(String key){
        try {
            Thread.sleep(1000*60*3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (servletContext.getAttribute(key)!=null){
            servletContext.removeAttribute(key);
        }
    }

}
