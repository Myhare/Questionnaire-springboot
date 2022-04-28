package com.ming.questionnaire_backstage.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
@ApiModel(value="Paper对象", description="")
public class Paper implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "问卷id")
    @TableId(value = "paper_id", type = IdType.ASSIGN_ID)
    private String paperId;

    @ApiModelProperty(value = "发布问卷的用户id")
    private String userId;

    @ApiModelProperty(value = "问卷标题")
    private String title;

    @ApiModelProperty(value = "问卷简介")
    private String introduce;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "填写问卷的人数")
    private Integer fillNumber;

    @ApiModelProperty(value = "发布状态（0未发布，1已发布）")
    private Integer paperStatus;

    @ApiModelProperty(value = "是否删除(0未删除，1已删除)")
    @TableLogic
    private Integer isdelete;

    public Paper(String paperId, String userId, String title, Date startTime,
                 Date endTime, Integer paperStatus, Integer isdelete) {
        this.paperId = paperId;
        this.userId = userId;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.paperStatus = paperStatus;
        this.isdelete = isdelete;
    }

    public Paper(String paperId, String userId, String title, String introduce,
                 Date startTime, Date endTime, Integer paperStatus, Integer isdelete) {
        this.paperId = paperId;
        this.userId = userId;
        this.title = title;
        this.introduce = introduce;
        this.startTime = startTime;
        this.endTime = endTime;
        this.paperStatus = paperStatus;
        this.isdelete = isdelete;
    }
}
