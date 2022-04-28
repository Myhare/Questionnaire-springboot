package com.ming.questionnaire_backstage.mapper;

import com.ming.questionnaire_backstage.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ming.questionnaire_backstage.pojo.views.admin.ViewUserInfo;
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
public interface UserMapper extends BaseMapper<User> {

    // 获取一个用户的权限
    List<String> selectPowerById(String userId);

    // 通过id修改用户的头像路径
    int updateHeaderById(@Param("userId") String userId,@Param("headerName") String headerName);

    // 分页查询用户
    List<ViewUserInfo> selectUserListPage(@Param("pageNum")int pageNum, @Param("pageSize")int pageSize);
    // 分页查询用户（带查询条件）
    List<ViewUserInfo> selectUserListPageW(@Param("pageNum")int pageNum,@Param("pageSize")int pageSize,@Param("query")String query);

    // 通过userid修改state
    int updateStateById(@Param("userId")String userId,@Param("state")int state);
}
