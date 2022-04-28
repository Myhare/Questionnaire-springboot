package com.ming.questionnaire_backstage.pojo.views;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel(value="答案对象", description="")
public class QuestionAnswer {

    private Integer questionId;  // 问题id
    private Integer questionType;//问题类型：1：单选题2：多选题3：简答题
    private String[] answer;//问题的答案：

}
