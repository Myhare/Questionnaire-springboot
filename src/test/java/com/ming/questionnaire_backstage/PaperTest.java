package com.ming.questionnaire_backstage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ming.questionnaire_backstage.mapper.PaperMapper;
import com.ming.questionnaire_backstage.pojo.Paper;
import com.ming.questionnaire_backstage.service.PaperService;
import com.ming.questionnaire_backstage.utils.UUIDUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class PaperTest {

    @Autowired
    private PaperMapper paperMapper;
    @Autowired
    private PaperService paperService;

    @Test
    void testInsertPaper(){
        Paper paper = new Paper(
                UUIDUtils.getUUID(),
                "dede2",
                "关于嘚嘚是不是傻逼的问卷调查",
                new Date(),
                new Date(),
                1,
                0);
        int i = paperMapper.insert(paper);
        if (i>0){
            System.out.println("添加问卷成功");
        }else {
            System.out.println("添加问卷失败");
        }
    }

    // 测试查询用户所有问卷
    @Test
    void testSelectPaperByUserId(){
        QueryWrapper<Paper> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id","5b2825bed36a458dae8a5031475e732e");  // 查询登录用户的问卷
        List<Paper> paperList = paperMapper.selectList(queryWrapper);
        for (Paper paper : paperList) {
            System.out.println(paper);
        }
    }


}
