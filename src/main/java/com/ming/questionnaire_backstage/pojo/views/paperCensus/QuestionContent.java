package com.ming.questionnaire_backstage.pojo.views.paperCensus;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="问卷统计中的问题结构", description="")
public class QuestionContent {

    private int questionId;  // 问题id
    private String QuestionTitle;   // 问题内容
    private int questionType;      // 问题类型
    private List<OptionAnswerCensus> answerCensusesList;   // 答案统计列表
}
