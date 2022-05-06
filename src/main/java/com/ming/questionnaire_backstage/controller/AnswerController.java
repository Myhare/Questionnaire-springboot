package com.ming.questionnaire_backstage.controller;


import com.ming.questionnaire_backstage.pojo.ResponseResult;
import com.ming.questionnaire_backstage.pojo.views.PaperAnswer;
import com.ming.questionnaire_backstage.pojo.views.admin.QueryInfo;
import com.ming.questionnaire_backstage.pojo.views.paperCensus.QuestionContent;
import com.ming.questionnaire_backstage.pojo.views.paperCensus.TextAreaDetails;
import com.ming.questionnaire_backstage.service.AnswerService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Ming
 * @since 2022-04-05
 */
@RestController
@RequestMapping("/answer")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    // 添加一个问卷答案,回答问卷
    @PostMapping("/addAnswer")
    public ResponseResult addAnswer(@RequestBody PaperAnswer paperAnswer){
        return answerService.addAnswer(paperAnswer);
    }

    // 通过问卷id获取答案
    @GetMapping("/getAnswerByPId/{paperId}")
    public ResponseResult<List<QuestionContent>> getAnswerByPId(@PathVariable(value = "paperId") String paperId){
        List<QuestionContent> questionContents = answerService.selectAnswerByPID(paperId);
        return new ResponseResult<List<QuestionContent>>(200,"查询成功",questionContents);
    }

    // 导出答案Excel
    @GetMapping("/exportAnswerExcel")
    public void exportAnswerExcel(HttpServletResponse response, @Param("paperName")String paperTitle,@Param("paperId")String paperId){
        answerService.answerExportExcel(response,paperTitle,paperId);
    }

    // 查询一个文本题的详细信息
    @GetMapping("/getTextAreaDetails")
    public ResponseResult<Map<String,Object>> getTextAreaDetails(@Param("questionId") String questionId,
                                                  @Param("queryInfo") QueryInfo queryInfo){
        List<TextAreaDetails> areaDetails = answerService.getTextAreaDetails(questionId,queryInfo);
        int total = answerService.getTextAreaTotal(questionId);  // 查询有多少人回答了这个问题
        HashMap<String, Object> map = new HashMap<>();
        map.put("areaDetails",areaDetails);
        map.put("total",total);
        return new ResponseResult<>(200,"查询成功",map);
    }

    // 导出文本题答案Excel
    @GetMapping("/exportTextAreaExcel")
    public void exportTextAreaExcel(HttpServletResponse response, @Param("questionId")String questionId){
        try {
            answerService.exportTextAreaExcel(response,questionId);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("导出异常");
        }
    }

}

