package com.ming.questionnaire_backstage.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ming.questionnaire_backstage.interfaces.AccessLimit;
import com.ming.questionnaire_backstage.pojo.ResponseResult;
import com.ming.questionnaire_backstage.utils.RedisUtil;
import com.ming.questionnaire_backstage.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class AccessLimitInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisUtil redisUtil;

    // 限制一个ip访问快速访问接口
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //判断请求是否属于方法的请求
        if(handler instanceof HandlerMethod){   // 如果handler是HandlerMethod的实例
            HandlerMethod hm = (HandlerMethod) handler;
            //获取方法中的注解,看是否有该注解
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if(accessLimit != null){
                int seconds = accessLimit.seconds();  // 多少秒
                int maxCount = accessLimit.maxCount();  // 最大访问数

                //从redis中获取用户访问的次数
                String ip = request.getHeader("x-forwarded-for");      // 有可能ip是代理的
                if(ip ==null || ip.length() ==0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("Proxy-Client-IP");
                }
                if(ip ==null || ip.length() ==0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("WL-Proxy-Client-IP");
                }
                if(ip ==null || ip.length() ==0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }

                Integer count = (Integer)redisUtil.get("AccessLimit:" + ip);
                if(count == null){
                    //第一次访问
                    redisUtil.set("AccessLimit:" + ip, 1, seconds);
                }else if(count < maxCount){
                    //加1
                    redisUtil.incr("AccessLimit:" + ip, 1);
                }else{
                    //超出访问次数
                    WebUtils.renderString(response,new ResponseResult(501,"操作太快了，请过会儿一段时间再操作"));
                    return false;
                }
            }
        }

        return super.preHandle(request, response, handler);  // 放行
    }
}
