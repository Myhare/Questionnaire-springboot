package com.ming.questionnaire_backstage.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author Ming
 * @since 2022-03-24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Question对象", description="")
public class Question implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "问题id")
    @TableId(value = "question_id", type = IdType.AUTO)
    private int questionId;

    @ApiModelProperty(value = "对应问卷的id")
    private String questionPId;

    @ApiModelProperty(value = "问题标题")
    private String questionTitle;

    @ApiModelProperty(value = "问题类型(1单选题，2多选题，3文本题)")
    private Integer questionType;

    @ApiModelProperty(value = "问题选项")
    private String questionOption;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "是否必填")
    private Integer isRequired;

    public Question(String questionPId, String questionTitle, Integer questionType, String questionOption, Date createTime) {
        this.questionPId = questionPId;
        this.questionTitle = questionTitle;
        this.questionType = questionType;
        this.questionOption = questionOption;
        this.createTime = createTime;
    }

    public Question(String questionPId, String questionTitle, Integer questionType, String questionOption, Date createTime, Integer isRequired) {
        this.questionPId = questionPId;
        this.questionTitle = questionTitle;
        this.questionType = questionType;
        this.questionOption = questionOption;
        this.createTime = createTime;
        this.isRequired = isRequired;
    }
}
