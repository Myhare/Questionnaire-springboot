package com.ming.questionnaire_backstage.config;

import com.ming.questionnaire_backstage.interceptor.AccessLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

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
        // 访问了/uploadFile/**路径后会自动映射到下面路径，用来获取用户登录后的头像
        registry.addResourceHandler("/uploadFile/**")  // /**就是找到的资源详细信息，会自动映射到下面的路径后面
                // /uploadFile/**表示在磁盘uploadFile目录下的所有资源会被解析为以下的路径
                .addResourceLocations("file:"+uploadPathImg);//定义图片存放路径
    }

    // 处理跨域请求，因为使用了SpringSecurity，所以还需要在springSecurity的配置类中设置允许跨域
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")//项目中的所有接口都支持跨域
                .allowedOrigins("*")//所有地址都可以访问，也可以配置具体地址
                .allowCredentials(true) //是否允许请求带有验证信息
                .allowedMethods("*")//"GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS"
                .allowedHeaders("*").maxAge(3600);// 跨域允许时间
    }

    // 添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessLimitInterceptor);    // 添加一个拦截器
    }

}
