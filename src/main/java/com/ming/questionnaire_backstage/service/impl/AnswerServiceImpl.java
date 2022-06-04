package com.ming.questionnaire_backstage.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ming.questionnaire_backstage.mapper.PaperMapper;
import com.ming.questionnaire_backstage.mapper.QuestionMapper;
import com.ming.questionnaire_backstage.pojo.*;
import com.ming.questionnaire_backstage.mapper.AnswerMapper;
import com.ming.questionnaire_backstage.pojo.views.PaperAnswer;
import com.ming.questionnaire_backstage.pojo.views.QuestionAnswer;
import com.ming.questionnaire_backstage.pojo.views.admin.QueryInfo;
import com.ming.questionnaire_backstage.pojo.views.paperCensus.OptionAnswerCensus;
import com.ming.questionnaire_backstage.pojo.views.paperCensus.QuestionContent;
import com.ming.questionnaire_backstage.pojo.views.paperCensus.TextAreaDetails;
import com.ming.questionnaire_backstage.service.AnswerService;
import com.ming.questionnaire_backstage.utils.ExcelUtil;
import com.ming.questionnaire_backstage.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ming
 * @since 2022-04-05
 */
@Service
@Transactional  // 开启事务管理
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    private AnswerMapper answerMapper;
    @Autowired
    private PaperMapper paperMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private RedisUtil redisUtil;

    // 添加一个答案
    @Override
    public ResponseResult addAnswer(PaperAnswer paperAnswer) {
        String paperId = paperAnswer.getPaperId();
        // 通过问卷id查询问卷的详细信息
        Paper paper = paperMapper.selectById(paperId);
        if (paper.getPaperStatus()==0){
            return new ResponseResult(401,"问卷未发布");
        }else if (paper.getPaperStatus()==-1){
            return new ResponseResult(401,"问卷已被封禁");
        }
        if (paper!=null){
            for (QuestionAnswer questionAnswer : paperAnswer.getAnswerList()) {
                Answer answer = null;
                if (questionAnswer.getQuestionType()==2){  // 如果是多选题
                    answer = new Answer(
                            paper.getUserId(),   // 发布问卷用户id
                            paperId,    // 问卷id
                            questionAnswer.getQuestionId(),   // 问题id
                            questionAnswer.getQuestionType(),  // 问题类型
                            JSONArray.toJSONString(
                                    Arrays.copyOfRange(questionAnswer.getAnswer(), 1, questionAnswer.getAnswer().length))   // 问题答案,将数组对象转化为字符串对象
                    );
                }else {  // 如果是单选题或者文本题
                    answer = new Answer(
                            paper.getUserId(),   // 发布问卷用户id
                            paperId,    // 问卷id
                            questionAnswer.getQuestionId(),   // 问题id
                            questionAnswer.getQuestionType(),  // 问题类型
                            JSONArray.toJSONString(questionAnswer.getAnswer())   // 问题答案,将数组对象转化为字符串对象
                    );
                }
                answerMapper.insert(answer);  // 将答案储存到数据库中
            }
            // redis中今天回答问卷的数量加一
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String answerCountKey = sdf.format(date) + ":answerCount";
            redisUtil.incr(answerCountKey,1);  // 今天回答问卷次数加一
            return new ResponseResult(200,"回答问卷成功");  // 说明答案添加成功
        }else {
            throw new RuntimeException("问卷id无效");
        }
    }

    // 查询一个表单的所有回答
    @Override
    public List<QuestionContent> selectAnswerByPID(String paperId) {
        Paper paper = paperMapper.selectById(paperId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        if (paper==null){
            throw new RuntimeException("问卷信息错误");
        }
        // 如果不正常当前登录用户和发布问卷的用户不是同一个，抛出异常
        if (!paper.getUserId().equals(loginUser.getUser().getUserId())){
            throw new RuntimeException("用户认证失败，不是本用户编写的问卷");
        }

        // 通过问卷id查询问卷中所有的答案
        Map<String, Object> selectMap = new HashMap<>();
        selectMap.put("question_p_id",paperId);
        List<Question> questionList = questionMapper.selectByMap(selectMap);  // 获取问卷中的所有答案列表

        // 创建返回的问题列表数据
        List<QuestionContent> questionContentList = new ArrayList<>();   // 返回值对象
        QuestionContent questionContent = null;     // 问题统计对象（循环在页面中的对象）
        for (Question question : questionList) {
            int questionId = question.getQuestionId();  // 获取问题id
            Integer questionType = question.getQuestionType();  // 获取问题类型

            List<OptionAnswerCensus> answerCensusesList = new ArrayList<>();;  // 刷新答案统计列表

            // 通过问题id查询到对应的answer队列
            QueryWrapper<Answer> answerQueryWrapper = new QueryWrapper<>();
            answerQueryWrapper.eq("question_id",questionId);
            List<Answer> answerList = answerMapper.selectList(answerQueryWrapper);  // 获取填写这个问题的所有答案
            List<String> optionList = JSONArray.parseArray(question.getQuestionOption(), String.class);  // 获取这个问题的选项列表

            questionContent = new QuestionContent();
            questionContent.setQuestionId(question.getQuestionId());
            questionContent.setQuestionTitle(question.getQuestionTitle());  // 设置问题的内容
            questionContent.setQuestionType(questionType);   // 设置问题的类型
            if (questionType == 1 || questionType == 2){
                // 对获取到的答案队列进行解析，创建一个map用来存储数据
                Map<Integer, Integer> map = new HashMap<>();  // map-> 选项下标：选项选中的次数
                // 将选项的详细信息添加到map中

                for (int i = 0; i < optionList.size(); i++) {
                    map.put(i,0);     // 将选项回答次数初始化，防止有些选项没人回答出现漏选
                }

                int count = 0;    // count 统计这个问题一共多少个人回答
                // 遍历获取的答案
                for (Answer answer : answerList) {
                    // 获取选择的答案
                    List<Integer> optionAnswerList = JSONArray.parseArray(answer.getAnswer(), Integer.class);
                    if (questionType == 1){
                        Integer key = optionAnswerList.get(0);   // key用来表示存放进map中的key，表示回答的问题的选项数组下标,这里是单选题，只有一个答案
                        // 使用前面创建的map统计每一个选项出现了多少次
                        if (map.containsKey(key)){  // 如果对应的map映射存在
                            map.put(key,map.get(key)+1);
                        }
                        count++;  // count用来统计一共有多少人回答了这个问题
                    }else{   // 如果是多选题
                        for (Integer key : optionAnswerList) {
                            // 使用前面创建的map统计每一个选项出现了多少次
                            if (map.containsKey(key)){  // 如果对应的map映射存在
                                map.put(key,map.get(key)+1);
                            }
                            // 使用前面创建的map统计每一个选项出现了多少次
                            if (map.containsKey(key)){  // 如果对应的map映射存在
                                map.put(key,map.get(key)+1);
                            }
                            count++;  // count用来统计一共有多少人回答了这个问题
                        }
                    }
                }
                // 将计算出来的统计数据封装进对象中
                for (Integer index : map.keySet()) {
                    Integer num = map.get(index);   // 回答了这个问题的数量
                    OptionAnswerCensus optionAnswerCensus =
                            new OptionAnswerCensus(optionList.get(index), num);
                    if (num==0){  // 说明这个问卷没有人填写
                        optionAnswerCensus.setPercentage("0%");
                    }else {   // 有人填写这个问卷，计算出百分比
                        optionAnswerCensus.setPercentage(getRatio(num, count));
                    }
                    answerCensusesList.add(optionAnswerCensus);
                }
            }else if (questionType == 3){  // 如果是文本题
                int count = answerList.size();    // count 统计这个问题一共多少个人回答
                answerCensusesList.add(new OptionAnswerCensus(count));
            }
            questionContent.setAnswerCensusesList(answerCensusesList);  // 设置
            questionContentList.add(questionContent);
        }

        return questionContentList;
    }

    // 删除一个问卷中的所有答案
    @Override
    public int deleteAnswerByPId(String paperId) {
        UpdateWrapper<Answer> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("paper_id",paperId);
        return answerMapper.delete(updateWrapper);
    }

    // 通过问卷id查询一个回答这个问卷的人数
    @Override
    public int getCountByPId(String paperId) {
        QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("paper_id",paperId);
        return answerMapper.selectCount(queryWrapper);
    }

    // 导出答案excel表
    @Override
    public void answerExportExcel(HttpServletResponse response, String paperTitle,String paperId) {
        // 通过id查询一个表单的所有答案
        List<QuestionContent> questionContents = selectAnswerByPID(paperId);
        try {
            // 设置返回的类型
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");  // 返回一个excel一个格式的输出流
            response.setHeader("Access-Control-Expose-Headers","Content-Disposition");  // 设置允许前端获取请求头
            response.setHeader("Content-Disposition","attachment; filename="+
                    java.net.URLEncoder.encode(paperTitle+"调查结果","UTF-8")+".xlsx"); // 设置附件的名字
            ExcelUtil.exportExcel(response,questionContents,paperTitle);  // 导出excel表
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 查询文本题详细答案
    @Override
    public List<TextAreaDetails> getTextAreaDetails(String questionId, QueryInfo queryInfo) {
        int pageNum = queryInfo.getPageNum();
        int pageSize = queryInfo.getPageSize();
        List<TextAreaDetails> areaDetailsList = answerMapper.getTextAreaDetails(questionId,(pageNum-1)*pageSize,pageSize);
        for (TextAreaDetails textAreaDetails : areaDetailsList) {
            textAreaDetails.setAnswer(JSONArray.parseArray(textAreaDetails.getAnswer(),String.class).get(0));
        }
        return areaDetailsList;
    }

    // 查询回答这个文本题的有多少人数
    @Override
    public int getTextAreaTotal(String questionId) {
        QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("question_id",questionId);
        return answerMapper.selectCount(queryWrapper);
    }

    // 导出所有文本题答案列表
    @Override
    public void exportTextAreaExcel(HttpServletResponse response, String questionId) throws IOException {
        Question question = questionMapper.selectById(questionId);
        String questionTitle = question.getQuestionTitle();  // 查询问题名称
        // 设置返回类型
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");  // 返回一个excel一个格式的输出流
        response.setHeader("Access-Control-Expose-Headers","Content-Disposition");
        response.setHeader("Content-Disposition","attachment; filename="+
                java.net.URLEncoder.encode(questionTitle+"回答结果","UTF-8")+".xlsx"); // 设置附件的名字
        List<TextAreaDetails> textAreaDetailsList = answerMapper.getAllTextAreaDetails(questionId);
        // 因为数据库中是用JSON格式储存的，这里将其转换成列表并且获取第一个答案就是文本题的答案
        for (TextAreaDetails textAreaDetails : textAreaDetailsList) {
            textAreaDetails.setAnswer(JSONArray.parseArray(textAreaDetails.getAnswer(),String.class).get(0));
        }
        ExcelUtil.writeEasyExcel(response,textAreaDetailsList);
    }

    // 输入两个数求百分比
    private String getRatio(Integer a,Integer b){
        //BigDecimal 格式化工具    保留两位小数
        NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMaximumFractionDigits(2);

        //除法结果保留4位小数，
        double per = new BigDecimal((float)a/b).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();

        //格式化为百分比字符串（自带百分号）
        return percent.format(per);
    }

}
