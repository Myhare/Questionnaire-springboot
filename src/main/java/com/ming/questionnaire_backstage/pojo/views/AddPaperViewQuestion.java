package com.ming.questionnaire_backstage.pojo.views;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddPaperViewQuestion {  // 问卷中的问题

    private int questionId;   // 问题id
    private Integer questionType;   // 问题类型
    private String questionTitle;   // 问题标题
    private List<String> questionOption;  // 问题选项
    private int isRequired;   // 是否必选
    private String[] answer;  // 用户选择的答案,第一个表示单选题和文本题的答案，如果有多选题更多选项存在数组后面

    public AddPaperViewQuestion(Integer questionType, String questionTitle, List<String> questionOption) {
        this.questionType = questionType;
        this.questionTitle = questionTitle;
        this.questionOption = questionOption;
        this.answer = new String[1];
    }

    public AddPaperViewQuestion(int questionId, Integer questionType, String questionTitle, List<String> questionOption,int isRequired) {
        this.questionId = questionId;
        this.questionType = questionType;
        this.questionTitle = questionTitle;
        this.questionOption = questionOption;
        this.answer = new String[1];
        this.isRequired = isRequired;
    }
}
