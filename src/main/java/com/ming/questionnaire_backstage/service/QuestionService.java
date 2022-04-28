package com.ming.questionnaire_backstage.service;

import com.ming.questionnaire_backstage.pojo.Question;

import java.util.List;

public interface QuestionService {

    // 通过问卷id查找对应的问题
    List<Question> selectQuestionByPId(String paperId);

}
