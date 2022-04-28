package com.ming.questionnaire_backstage.pojo.views;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel(value="提交答案对象", description="")
public class PaperAnswer {

    private String paperId;    // 问卷id
    private List<QuestionAnswer> answerList;   // 答案列表

}
