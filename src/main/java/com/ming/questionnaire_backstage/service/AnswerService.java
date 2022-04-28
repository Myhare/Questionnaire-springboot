package com.ming.questionnaire_backstage.service;

import com.ming.questionnaire_backstage.pojo.views.PaperAnswer;
import com.ming.questionnaire_backstage.pojo.views.QuestionAnswer;
import com.ming.questionnaire_backstage.pojo.views.admin.QueryInfo;
import com.ming.questionnaire_backstage.pojo.views.paperCensus.QuestionContent;
import com.ming.questionnaire_backstage.pojo.views.paperCensus.TextAreaDetails;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ming
 * @since 2022-04-05
 */
@Transactional  // 开启事务管理
public interface AnswerService {

    // 添加一个答案
    int addAnswer(PaperAnswer paperAnswer);

    // 查询一个问卷的所有回答
    List<QuestionContent> selectAnswerByPID(String paperId);

    // 删除一个表中的所有答案
    int deleteAnswerByPId(String paperId);

    // 通过问卷id查询一个回答这个问卷的人数
    int getCountByPId(String paperId);

    // 导出答案excel表格
    void answerExportExcel(HttpServletResponse response,String paperTitle,String paperId);

    // 通过questionId查询答案对应的详细信息
    List<TextAreaDetails> getTextAreaDetails(String questionId, QueryInfo queryInfo);

    // 获取文本题答案数量
    int getTextAreaTotal(String questionId);

    // 导出所有文本题答案excel
    void exportTextAreaExcel(HttpServletResponse response,String questionId) throws IOException;
}
