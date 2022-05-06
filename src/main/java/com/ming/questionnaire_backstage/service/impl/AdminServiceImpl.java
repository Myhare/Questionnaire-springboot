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
import com.ming.questionnaire_backstage.utils.RedisUtil;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private RedisUtil redisUtil;

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
        List<String> permissions = loginUser.getPermissions(); // 获取权限
        // 如果用户没有sys:admin权限，返回权限不足提示
        if (!permissions.contains("sys::admin")){
            return new ResponseResult(HttpStatus.UNAUTHORIZED.value(),"该用户没有权限登录后台，请登录管理员账户或者切换前台登录");
        }

        String userId = loginUser.getUser().getUserId();
        String token = JwtUtil.createJWT(userId);

        // 将loginUser存入到redis中
        redisUtil.set("login:"+userId,loginUser,60*60*3);  // 设置三个小时的过期时间

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

    // 获取最近七天用户登录的人数
    @Override
    public Map<String,Object> getUserLoginCount() {
        Date date = new Date();
        SimpleDateFormat redisSdf = new SimpleDateFormat("yyyy-MM-dd");   // 用来拼接redis中的key
        SimpleDateFormat mapSdf = new SimpleDateFormat("MM-dd");
        HashMap<String, Object> map = new HashMap<>();
        ArrayList<String> dateList = new ArrayList<>();   // 时间列表,前端统计图下标
        ArrayList<Integer> countList = new ArrayList<>();  // 登录人数列表
        Calendar calendar = Calendar.getInstance();   // 用来计算格式化日期
        for (int i = -6; i <= 0; i++) {
            calendar.setTime(date);
            calendar.add(Calendar.DATE,i);  // 减去i天，一共循环7次
            String redisKey = redisSdf.format(calendar.getTime())+":userCount";
            countList.add(redisUtil.get(redisKey) == null ? 0 : (Integer) redisUtil.get(redisKey));
            dateList.add(mapSdf.format(calendar.getTime()));
        }
        map.put("dateList",dateList);
        map.put("countList",countList);
        return map;
    }

    // 获取最近七天问卷发布情况
    @Override
    public Map<String, Object> getPaperReleaseCount() {
        Date date = new Date();
        SimpleDateFormat redisSdf = new SimpleDateFormat("yyyy-MM-dd");   // 用来拼接redis中的key
        SimpleDateFormat mapSdf = new SimpleDateFormat("MM-dd");
        HashMap<String, Object> map = new HashMap<>();
        ArrayList<String> dateList = new ArrayList<>();   // 时间列表,前端统计图下标
        ArrayList<Integer> countList = new ArrayList<>();  // 登录人数列表
        Calendar calendar = Calendar.getInstance();   // 用来计算格式化日期
        for (int i = -6; i <= 0; i++) {
            calendar.setTime(date);
            calendar.add(Calendar.DATE,i);  // 减去i天，一共循环7次
            String redisKey = redisSdf.format(calendar.getTime())+":paperCount";
            countList.add(redisUtil.get(redisKey) == null ? 0 : (Integer) redisUtil.get(redisKey));
            dateList.add(mapSdf.format(calendar.getTime()));
        }
        map.put("dateList",dateList);
        map.put("countList",countList);
        return map;
    }

    // 获取七天内问卷回答情况
    @Override
    public Map<String, Object> getAnswerReleaseCount() {
        Date date = new Date();
        SimpleDateFormat redisSdf = new SimpleDateFormat("yyyy-MM-dd");   // 用来拼接redis中的key
        SimpleDateFormat mapSdf = new SimpleDateFormat("MM-dd");
        HashMap<String, Object> map = new HashMap<>();
        ArrayList<String> dateList = new ArrayList<>();   // 时间列表,前端统计图下标
        ArrayList<Integer> countList = new ArrayList<>();  // 登录人数列表
        Calendar calendar = Calendar.getInstance();   // 用来计算格式化日期
        for (int i = -6; i <= 0; i++) {
            calendar.setTime(date);
            calendar.add(Calendar.DATE,i);  // 减去i天，一共循环7次
            String redisKey = redisSdf.format(calendar.getTime())+":answerCount";
            countList.add(redisUtil.get(redisKey) == null ? 0 : (Integer) redisUtil.get(redisKey));
            dateList.add(mapSdf.format(calendar.getTime()));
        }
        map.put("dateList",dateList);
        map.put("countList",countList);
        return map;
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
