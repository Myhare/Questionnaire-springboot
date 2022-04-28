package com.ming.questionnaire_backstage.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan("com.ming.questionnaire_backstage.mapper")  // 扫描包，MP扫描了 就不需要在主启动类上扫描
@Configuration // 表示是配置类
@EnableTransactionManagement  // 自动管理事务,默认也是开启的
public class MybatisPlusConfig {

    // 分页插件
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }

}
