package com.ming.questionnaire_backstage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ming.questionnaire_backstage.mapper.QuestionMapper;
import com.ming.questionnaire_backstage.pojo.Question;
import com.ming.questionnaire_backstage.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    // 通过问卷id查找对应的问题
    @Override
    public List<Question> selectQuestionByPId(String paperId) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("question_p_id",paperId);
        return questionMapper.selectList(queryWrapper);
    }
}
