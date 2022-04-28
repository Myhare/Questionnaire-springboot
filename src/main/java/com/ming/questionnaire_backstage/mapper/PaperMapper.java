package com.ming.questionnaire_backstage.mapper;

import com.ming.questionnaire_backstage.pojo.Paper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ming.questionnaire_backstage.pojo.views.admin.ViewPaperInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Ming
 * @since 2022-03-24
 */
@Repository
public interface PaperMapper extends BaseMapper<Paper> {

    // 物理删除一个paper表
    int physicsDeleteById(String paperId);

    // 分页查询问卷列表
    List<ViewPaperInfo> selectPaperListPage(@Param("pageNum")int pageNum,@Param("pageSize")int pageSize);
    // 分页查询问卷列表
    List<ViewPaperInfo> selectPaperListPageW(@Param("pageNum")int pageNum,@Param("pageSize")int pageSize,@Param("query")String query);
}
