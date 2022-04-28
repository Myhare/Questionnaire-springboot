package com.ming.questionnaire_backstage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAsync  // 开启异步请求
public class QuestionnaireBackstageApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuestionnaireBackstageApplication.class, args);
    }

}
