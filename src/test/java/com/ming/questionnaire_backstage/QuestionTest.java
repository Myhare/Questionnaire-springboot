package com.ming.questionnaire_backstage;

import com.alibaba.fastjson.JSONArray;
import com.ming.questionnaire_backstage.mapper.QuestionMapper;
import com.ming.questionnaire_backstage.pojo.Question;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class QuestionTest {
    @Autowired
    private QuestionMapper questionMapper;

    // 测试添加问卷
    @Test
    void testInsertPaper(){
        List<String> questionList = new ArrayList<String>(Arrays.asList("是傻逼","确实是傻逼"));
        Question addQuestion = new Question(
                "111", "嘚嘚是傻逼吗",
                1,
                JSONArray.toJSONString(questionList),  // 将选项list对象转化为JSON字符串储存到数据库中
                new Date());
        // 将addQuestion储存到数据库中
        int i = questionMapper.insert(addQuestion);
        if (i>0){
            System.out.println("添加问题成功");
        }else {
            System.out.println("添加问题失败");
        }

    }

}
