package com.ming.questionnaire_backstage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ming.questionnaire_backstage.mapper.PaperMapper;
import com.ming.questionnaire_backstage.mapper.UserMapper;
import com.ming.questionnaire_backstage.pojo.LoginUser;
import com.ming.questionnaire_backstage.pojo.Paper;
import com.ming.questionnaire_backstage.pojo.ResponseResult;
import com.ming.questionnaire_backstage.pojo.User;
import com.ming.questionnaire_backstage.pojo.views.admin.QueryInfo;
import com.ming.questionnaire_backstage.pojo.views.admin.ViewPaperInfo;
import com.ming.questionnaire_backstage.pojo.views.admin.ViewUserInfo;
import com.ming.questionnaire_backstage.service.AdminService;
import com.ming.questionnaire_backstage.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PaperMapper paperMapper;

    // 用户获取头像路径
    @Value("${web.get-head-path}")
    private String getHeadPath;

    // 后台登录
    @Override
    public ResponseResult adminLogin(User user) {
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(user.getUserName(), user.getUserPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        // 如果认证没通过，给出对应提示
        if (authenticate == null) {  // 说明认证没有通过，抛出异常让全局过滤器捕获
            throw new RuntimeException("登录失败,用户名或密码错误");
        }
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        List<String> permissions = loginUser.getPermissions();
        // 如果用户没有sys:admin权限，返回权限不足提示
        if (!permissions.contains("sys::admin")){
            return new ResponseResult(HttpStatus.UNAUTHORIZED.value(),"该用户没有权限登录后台，请登录管理员账户或者切换前台登录");
        }

        String userId = loginUser.getUser().getUserId();
        String token = JwtUtil.createJWT(userId);

        // 通过userid查询用户详细信息传入前端
        User userInfo = userMapper.selectById(userId);
        // 将"/uploadFile/"设置到环境配置中，修改头像服务里面也要修改
        if (userInfo.getUserHeadPath()!=null){
            userInfo.setUserHeadPath(getHeadPath+userInfo.getUserHeadPath());  // 更新用户头像信息
        }
        Map<String, Object> map = new HashMap<>();
        map.put("token",token);
        map.put("userInfo",userInfo);
        return new ResponseResult(200,"登录成功",map);
    }

    // 后台查询用户
    @Override
    public List<ViewUserInfo> getUserList(QueryInfo queryInfo) {
        String query = queryInfo.getQuery();
        int pageNum = queryInfo.getPageNum();
        int pageSize = queryInfo.getPageSize();
        // 如果用户进行了查询，添加查询条件
        if (!StringUtils.isEmpty(query)){
            return userMapper.selectUserListPageW((pageNum-1)*pageSize, pageSize, query);  // 因为从pageNum开始查询，前端请求从1开始，数据库从0开始。
        }else {
            return userMapper.selectUserListPage((pageNum-1)*pageSize, pageSize);
        }
    }

    // 查询一共有多少个用户
    @Override
    public int getUserCount(String query) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("user_name",query);
        return userMapper.selectCount(queryWrapper);
    }

    // 修改一个用户状态
    @Override
    public int updateStateById(String userId, int state) {
        return userMapper.updateStateById(userId, state);
    }

    // 分页查询问卷列表
    @Override
    public List<ViewPaperInfo> getPaperList(QueryInfo queryInfo) {
        String query = queryInfo.getQuery();
        int pageNum = queryInfo.getPageNum();
        int pageSize = queryInfo.getPageSize();
        if (StringUtils.isEmpty(query)){
            return paperMapper.selectPaperListPage((pageNum-1)*pageSize, pageSize);
        }else {
            return paperMapper.selectPaperListPageW((pageNum-1)*pageSize,pageSize,query);
        }
    }
    // 获取一共有多少个问卷
    @Override
    public int getPaperCount(String query) {
        QueryWrapper<Paper> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("title",query);
        return paperMapper.selectCount(queryWrapper);
    }

    // 封禁一个问卷
    @Override
    public int banPaperById(String paperId) {
        UpdateWrapper<Paper> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("paper_id",paperId)
                .set("paper_status",-1);
        return paperMapper.update(null, updateWrapper);
    }

    // 取消一个问卷的封禁
    @Override
    public int noBanPaperById(String paperId) {
        UpdateWrapper<Paper> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("paper_id",paperId)
                .set("paper_status",1);
        return paperMapper.update(null, updateWrapper);
    }
}
