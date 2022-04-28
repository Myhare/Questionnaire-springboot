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
 * @since 2022-04-05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Answer对象", description="")
public class Answer implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "答案id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "发布问卷的用户id")
    private String toUserId;

    @ApiModelProperty(value = "回答的问卷id")
    private String paperId;

    @ApiModelProperty(value = "回答的问题id")
    private Integer questionId;

    @ApiModelProperty(value = "问题类型")
    private Integer questionType;

    @ApiModelProperty(value = "问题答案")
    private String answer;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    public Answer(String toUserId, String paperId, Integer questionId,Integer questionType,
                  String answer) {
        this.toUserId = toUserId;
        this.paperId = paperId;
        this.questionId = questionId;
        this.questionType = questionType;
        this.answer = answer;
    }
}
