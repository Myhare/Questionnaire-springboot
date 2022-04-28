package com.ming.questionnaire_backstage.pojo.views.paperCensus;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="文本题详细信息", description="")
public class TextAreaDetails implements Serializable {

    @ExcelProperty("答案")
    @ColumnWidth(100)   // 指定列对应的宽度
    private String answer;
    @ExcelProperty("填写时间")
    @DateTimeFormat("yyyy/M/dd") // 转换日期格式，这样导出excel表格的时候不会乱码
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")  // 返回前端的时候格式化
    private Date answerTime;

}
