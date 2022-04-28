package com.ming.questionnaire_backstage.pojo.views.paperCensus;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="问卷选项答案统计对象", description="")
public class OptionAnswerCensus {

    private String optionName;  // 选项内容
    private int num;            // 回答数量
    private String percentage;  // 回答这个问题所占的百分比

    public OptionAnswerCensus(String optionName, int num) {
        this.optionName = optionName;
        this.num = num;
    }

    // 文本题在页面中只需要展示最基本的有多少条数据就可以了
    public OptionAnswerCensus(int num) {
        this.num = num;
    }
}
