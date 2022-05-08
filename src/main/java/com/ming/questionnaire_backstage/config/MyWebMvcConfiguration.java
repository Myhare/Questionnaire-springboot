package com.ming.questionnaire_backstage.config;

import com.ming.questionnaire_backstage.interceptor.AccessLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class MyWebMvcConfiguration implements WebMvcConfigurer {
    @Value("${web.upload-path}")
    private String uploadPathImg;

    @Autowired
    private AccessLimitInterceptor accessLimitInterceptor;

    //配置本地文件映射到url上
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //重写方法
        //修改tomcat 虚拟映射
        // 访问了/uploadFile/**路径后会自动映射到下面路径
        registry.addResourceHandler("/uploadFile/**")
                // /apple/**表示在磁盘apple目录下的所有资源会被解析为以下的路径
                .addResourceLocations("file:"+uploadPathImg);//定义图片存放路径
    }

    // 添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessLimitInterceptor);    // 添加一个拦截器
    }

}
