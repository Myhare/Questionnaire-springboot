package com.ming.questionnaire_backstage.service;

import com.ming.questionnaire_backstage.pojo.ResponseResult;
import com.ming.questionnaire_backstage.pojo.User;
import com.ming.questionnaire_backstage.pojo.views.admin.QueryInfo;
import com.ming.questionnaire_backstage.pojo.views.admin.ViewPaperInfo;
import com.ming.questionnaire_backstage.pojo.views.admin.ViewUserInfo;

import java.util.List;
import java.util.Map;

public interface AdminService {

    // 后台登录
    ResponseResult adminLogin(User user);

    // 分页查询用户
    List<ViewUserInfo> getUserList(QueryInfo queryInfo);
    // 查询一共有多少个用户
    int getUserCount(String query);
    // 修改一个用户的状态
    int updateStateById(String userId,int state);

    // 分页查询问卷列表
    List<ViewPaperInfo> getPaperList(QueryInfo queryInfo);
    // 查询一共有多少个问卷
    int getPaperCount(String query);
    // 封禁一个问卷
    int banPaperById(String paperId);
    // 取消一个问卷的封禁
    int noBanPaperById(String paperId);

    // 获取最近七天用户登录的人数
    Map<String,Object> getUserLoginCount();

    // 获取最近七天问卷发布情况
    Map<String, Object> getPaperReleaseCount();

    // 获取最近七天问卷回答情况
    Map<String, Object> getAnswerReleaseCount();
}
