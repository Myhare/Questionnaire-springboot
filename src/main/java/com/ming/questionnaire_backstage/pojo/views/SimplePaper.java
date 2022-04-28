package com.ming.questionnaire_backstage.pojo.views;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)    // 开启链式编程
@ApiModel(value="简单页面问卷存储，用来存放页面左侧问卷信息", description="")
public class SimplePaper {
    private String paperId;   // 问卷id
    private String userId;   // 用户id
    private String paperTitle;  // 问卷标题
    private Integer status;   // 状态
    private int paperState;
}
