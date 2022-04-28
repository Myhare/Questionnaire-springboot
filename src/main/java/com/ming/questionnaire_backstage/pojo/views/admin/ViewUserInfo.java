package com.ming.questionnaire_backstage.pojo.views.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 显示在前端的用户信息
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewUserInfo {

    private String userId;   // 用户id
    private String userName;
    private String UserEmail;
    private String phone;
    private String name;   // 身份
    private int state;   // 状态（是否被封禁）
}
