package com.ming.questionnaire_backstage.pojo.views;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewUserInfo {

    private String userId;
    private String userName;
    private String userPassword;
    private String userEmail;
    private String phone;
    private String userIntroduce;

}
