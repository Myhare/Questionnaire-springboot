package com.ming.questionnaire_backstage.pojo.views;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)    // 开启链式编程
@ApiModel(value="添加Paper页面存储，用来临时存放数据", description="")
public class AddViewPaper {
    private String paperId;   // 问卷id
    private String userId;   // 用户id
    private String paperTitle;  // 问卷标题
    private String paperIntroduce;  // 问卷简介
    private Date startTime;   // 开始时间
    private Date endTime;    // 结束时间
    private Integer status;   // 状态
    private List<AddPaperViewQuestion> questionList;  // 问题列表
}
