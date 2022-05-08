package com.ming.questionnaire_backstage.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)   // 可以在方法上加上这个注释
public @interface AccessLimit {

    int seconds();   // 多少秒
    int maxCount();  // 请求最多有多少次

}
