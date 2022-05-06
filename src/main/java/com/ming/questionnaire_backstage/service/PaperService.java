package com.ming.questionnaire_backstage.service;


import com.ming.questionnaire_backstage.pojo.views.SimplePaper;
import com.ming.questionnaire_backstage.pojo.views.AddViewPaper;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PaperService {

    // 添加或者修改一个问卷
    int addOrUpdatePaper(AddViewPaper addViewPaper);

    // 查找一个用户的所有问卷
    List<SimplePaper> getSimplePaperByUId(String userId);

    // 通过id删除一个问卷
    boolean deletePaperById(String paperId);

    // 通过id查找问卷
    AddViewPaper selectPaperById(String paperId);

    // 通过id查询问卷的数量(判断问卷是否存在)
    int getPaperCount(String paperId);

    // 查询一个问卷的状态
    int selectPaperStatus(String paperId);

    // 发布一个问卷
    int issuePaper(String paperId);

    // 取消发布问卷
    int cancelIssuePaper(String paperId);

    // 查询一个问卷的状态
    int queryPStateById(String paperId, HttpServletRequest request);
}
