package com.ming.questionnaire_backstage.pojo.views.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryInfo {
    // 查询用户的对象
    private String query;   // 查询条件
    private int pageNum;    // 查询当前的页数
    private int pageSize;   // 查询的数量

}
