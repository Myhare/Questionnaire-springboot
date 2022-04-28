package com.ming.questionnaire_backstage.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ming.questionnaire_backstage.mapper.AnswerMapper;
import com.ming.questionnaire_backstage.mapper.PaperMapper;
import com.ming.questionnaire_backstage.mapper.QuestionMapper;
import com.ming.questionnaire_backstage.pojo.Answer;
import com.ming.questionnaire_backstage.pojo.Paper;
import com.ming.questionnaire_backstage.pojo.Question;
import com.ming.questionnaire_backstage.pojo.views.AddPaperViewQuestion;
import com.ming.questionnaire_backstage.pojo.views.SimplePaper;
import com.ming.questionnaire_backstage.pojo.views.AddViewPaper;
import com.ming.questionnaire_backstage.service.AnswerService;
import com.ming.questionnaire_backstage.service.PaperService;
import com.ming.questionnaire_backstage.service.QuestionService;
import com.ming.questionnaire_backstage.utils.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PaperServiceImpl implements PaperService {

    @Autowired
    private PaperMapper paperMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private AnswerService answerService;


    // 添加或者修改一个问卷
    @Override
    public int addOrUpdatePaper(AddViewPaper addViewPaper) {
        boolean isUpdate = false;
        String paperId = StringUtils.isEmpty(addViewPaper.getPaperId()) ? null: addViewPaper.getPaperId();
        // 如果paperId不为空，说明已经有值了，先将表中paperId的值删除，再重新添加
        if (paperId!=null) {
            questionMapper.physicsDeleteByPId(paperId);  // 物理删除问题表中的的数据
            isUpdate = true;
        }else {
            paperId = UUIDUtils.getUUID();
        }
        // 重新添加一个问卷
        // 将问卷信息从viewPaper中分离出来
        Paper paper =
                new Paper(paperId, addViewPaper.getUserId(), addViewPaper.getPaperTitle(),
                        addViewPaper.getPaperIntroduce(),null, null, addViewPaper.getStatus(), 0);
        // 更新表中的数据
        // 如果是创建表，就添加一个新的表，如果是更新表，就更新原有的表
        if (isUpdate){  // 如果是更新表
            // 删除这个表填写的所有答案
            answerService.deleteAnswerByPId(paperId);
            paperMapper.updateById(paper);
        }else {
            paperMapper.insert(paper);
        }
        // 将问题信息分离出来
        for (AddPaperViewQuestion question : addViewPaper.getQuestionList()) {
            Question addQuestion = new Question(
                    paperId, question.getQuestionTitle(),
                    question.getQuestionType(),
                    JSONArray.toJSONString(question.getQuestionOption()),  // 将问题选项转化为JSON字符串储存到数据库中
                    new Date(), // 设置创建时间
                    question.getIsRequired());  // 设置是否是必填
            // 将addQuestion储存到数据库中
            questionMapper.insert(addQuestion);
        }
        if (isUpdate){
            return 1;  // 说明更新了问卷
        }else {
            return 2;   // 添加了问卷
        }
    }

    // 查询一个用户的所有问卷
    @Override
    public List<SimplePaper> getSimplePaperByUId(String userId) {
        QueryWrapper<Paper> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);  // 查询登录用户的问卷
        queryWrapper.orderByDesc("update_time");  // 根据创建时间排序
        List<Paper> paperList = paperMapper.selectList(queryWrapper);
        // 将paperList查询到的paper封装成简单的格式
        ArrayList<SimplePaper> simplePapers = new ArrayList<>();
        SimplePaper simplePaper = null;
        for (Paper paper : paperList) {
            simplePaper = new SimplePaper(paper.getPaperId(),paper.getUserId(),paper.getTitle(),paper.getPaperStatus(),paper.getPaperStatus());
            simplePapers.add(simplePaper);
        }
        return simplePapers;
    }

    // 通过问卷id删除一个问卷
    @Override
    public boolean deletePaperById(String paperId) {
        int i = paperMapper.deleteById(paperId);
        if (i>0){
            return true;
        }else {
            return false;
        }
    }

    // 通过id查找问卷
    @Override
    public AddViewPaper selectPaperById(String paperId) {
        AddViewPaper addViewPaper = null;
        try {
            Paper paper = paperMapper.selectById(paperId);
            // 查找问题列表
            List<Question> questions = questionService.selectQuestionByPId(paperId);
            // 将问题列表封装起来问题列表
            ArrayList<AddPaperViewQuestion> viewQuestionList = new ArrayList<>();
            AddPaperViewQuestion q = null;
            for (Question question : questions) {
                List<String> optionList = JSONArray.parseArray(question.getQuestionOption(), String.class);  // 将数据库中查找的string类型还原成List类型
                q = new AddPaperViewQuestion(
                        question.getQuestionId(),
                        question.getQuestionType(),
                        question.getQuestionTitle(),
                        optionList,
                        question.getIsRequired());
                viewQuestionList.add(q);
            }

            addViewPaper = new AddViewPaper();
            addViewPaper
                    .setPaperId(paperId)
                    .setPaperTitle(paper.getTitle())
                    .setPaperIntroduce(paper.getIntroduce())
                    .setQuestionList(viewQuestionList);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("查询问卷和问题异常");
        }
        return addViewPaper;
    }

    // 查询一个问卷的数量
    @Override
    public int getPaperCount(String paperId) {
        Paper paper = paperMapper.selectById(paperId);
        if (paper!=null){
            return 1;
        }else {
            return 0;
        }
    }

    // 查询一个问卷是否被封禁
    @Override
    public int selectPaperStatus(String paperId) {
        Paper paper = paperMapper.selectById(paperId);
        return paper.getPaperStatus();   // 如果是-1就代表被风景
    }

    // 发布一个问卷
    @Override
    public int issuePaper(String paperId) {
        UpdateWrapper<Paper> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("paper_id",paperId)
                .set("paper_status",1);
        return paperMapper.update(null,updateWrapper);
    }

    // 取消发布问卷
    @Override
    public int cancelIssuePaper(String paperId) {
        UpdateWrapper<Paper> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("paper_id",paperId)
                .set("paper_status",0);
        return paperMapper.update(null,updateWrapper);
    }
}
