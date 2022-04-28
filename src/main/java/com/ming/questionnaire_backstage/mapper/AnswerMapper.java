package com.ming.questionnaire_backstage.mapper;

import com.ming.questionnaire_backstage.pojo.Answer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ming.questionnaire_backstage.pojo.views.paperCensus.TextAreaDetails;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Ming
 * @since 2022-04-05
 */
@Repository
public interface AnswerMapper extends BaseMapper<Answer> {

    // 查询文本题详细信息
    List<TextAreaDetails> getTextAreaDetails(@Param("questionId") String questionId,@Param("pageNum") int pageNum,@Param("pageSize") int pageSize);

    // 查询所有文本题详细信息
    List<TextAreaDetails> getAllTextAreaDetails(@Param("questionId") String questionId);
}
