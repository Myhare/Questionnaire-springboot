package com.ming.questionnaire_backstage;

import com.ming.questionnaire_backstage.mapper.AnswerMapper;
import com.ming.questionnaire_backstage.pojo.Answer;
import com.ming.questionnaire_backstage.pojo.views.PaperAnswer;
import com.ming.questionnaire_backstage.pojo.views.QuestionAnswer;
import com.ming.questionnaire_backstage.pojo.views.paperCensus.TextAreaDetails;
import com.ming.questionnaire_backstage.service.AnswerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class AnswerTest {

    @Autowired
    private AnswerMapper answerMapper;
    @Autowired
    private AnswerService answerService;

    // 测试添加问卷
    @Test
    public void testInsertAnswer(){

        Answer answer = new Answer(
                "发布问卷用户id","问卷id",1,11,"答案");
        int i = answerMapper.insert(answer);
        if (i > 0){
            System.out.println("插入数据成功");
        }else {
            System.out.println("插入数据失败");
        }
    }

    // 测试添加问卷2
    @Test
    public void testInsertAnswer2(){

        List<QuestionAnswer> list = new ArrayList<>();
        list.add(new QuestionAnswer(1,1,new String[]{"问题答案"}));
        list.add(new QuestionAnswer(1,1,new String[]{"问题答案"}));
        PaperAnswer answerList = new PaperAnswer("4ea046c25f7c4bd3816c8b9c94ee35ee", list);
        int i = answerService.addAnswer(answerList);
        if (i>0){
            System.out.println("问卷添加成功");
        }else {
            System.out.println("问卷添加失败");
        }
    }
    // 测试问卷是否有人填写
    @Test
    void testGetPaperAnswer(){
        int count = answerService.getCountByPId("bb411fc102c44bc785d7933dbcfb4584");
        if (count>0){
            System.out.println("有用户填写");
        }else {
            System.out.println("没有用户填写");
        }
    }

    // 测试查询文本题详细信息
    @Test
    void testGetTextAreaDetail(){
        List<TextAreaDetails> textAreaDetails = answerService.getTextAreaDetails("137",null);
        for (TextAreaDetails textAreaDetail : textAreaDetails) {
            System.out.println(textAreaDetail);
        }
    }

    // 测试查询文本题回答数量
    @Test
    void testGetTextAreaTotal(){
        System.out.println(answerService.getTextAreaTotal("137"));
    }

    // 测试查询文本题回答导出信息
    @Test
    void testGetTextAreaExport(){
        try {
            answerService.exportTextAreaExcel(null,"137");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
