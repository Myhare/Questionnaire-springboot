package com.ming.questionnaire_backstage.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.ming.questionnaire_backstage.pojo.views.ecxel.ExportExcel;
import com.ming.questionnaire_backstage.pojo.views.paperCensus.OptionAnswerCensus;
import com.ming.questionnaire_backstage.pojo.views.paperCensus.QuestionContent;
import com.ming.questionnaire_backstage.pojo.views.paperCensus.TextAreaDetails;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ExcelUtil {

    /**
     * 导出简单的Excel
     * @param response 返回对象
     * @param list Excel表中的记录
     */
    public static void writeEasyExcel(HttpServletResponse response, List<TextAreaDetails> list) throws IOException {

        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();
        // 定义工作表对象
        // writerSheet两个对象，分别是工作表下标，和工作表名称
        WriteSheet sheet = EasyExcel.writerSheet(0, "sheet").head(TextAreaDetails.class).build();

        // 将list写入到定义的sheet表中
        excelWriter.write(list,sheet);
        excelWriter.finish();   // 关闭输出流
    }



    /**
     * 将列表答案导出excel表格
     * @param response 返回对象
     * @param answerList 答案列表
     * @param paperName 表格名称
     */
    public static void exportExcel(HttpServletResponse response, List<QuestionContent> answerList, String paperName) throws IOException {

        // 创建一个工作簿
        Workbook workbook = new SXSSFWorkbook();
        // 创建一个工作表
        Sheet sheet = workbook.createSheet();

        // 创建第一行
        Row row1 = sheet.createRow(0);
        // 创建单元格(0,0)
        Cell cell1 = row1.createCell(0);
        cell1.setCellValue("问卷名称："+paperName);

        int index = 1;  // 题号，初始是1
        int rowIndex = 2;  // 行数，第一行为问卷标题，现在从第三行开始
        int cellIndex = 0;  // 默认从第0列开始

        // 从第三行开始，解析成对应的表格
        for (QuestionContent questionContent:answerList){
            String qType = "";
            switch (questionContent.getQuestionType()){
                case 1: qType = "[单选题]"; break;
                case 2: qType = "[多选题]"; break;
                case 3: qType = "[文本题]"; break;
            }
            sheet.createRow(rowIndex++).createCell(0).setCellValue(qType + questionContent.getQuestionTitle());  // 设置问题内容为单独的一行
            if (questionContent.getQuestionType()==3){
                sheet.createRow(rowIndex++).createCell(0).setCellValue("文本题请在统计界面中单独导出");
                rowIndex++;
                continue;
            }
            Row tipRow = sheet.createRow(rowIndex++);
            tipRow.createCell(0).setCellValue("选项");
            tipRow.createCell(1).setCellValue("数量");
            tipRow.createCell(2).setCellValue("所占比例");
            for (OptionAnswerCensus optionAnswerCensus : questionContent.getAnswerCensusesList()) {
                Row row = sheet.createRow(rowIndex++);  // 创建一 个新的行
                // 创建列
                row.createCell(cellIndex++).setCellValue(optionAnswerCensus.getOptionName());  // 选项
                row.createCell(cellIndex++).setCellValue(optionAnswerCensus.getNum());         // 数量
                row.createCell(cellIndex).setCellValue(optionAnswerCensus.getPercentage());  // 百分比
                cellIndex = 0;  // 重置列的下标
            }
            // 空一行
            rowIndex++;
        }
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.close();
        // 清除临时文件
        ((SXSSFWorkbook)workbook).dispose();
    }

}
