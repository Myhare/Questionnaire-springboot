package com.ming.questionnaire_backstage.pojo.views.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

// 显示在前端的问卷信息
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewPaperInfo {

    private String paperId;
    private String title;
    private String userName;  // 发布问卷的用户名
    private int paperStatus;   // 问卷状态
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;  // 发布时间

}
