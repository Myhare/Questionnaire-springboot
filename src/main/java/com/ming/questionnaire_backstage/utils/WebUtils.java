package com.ming.questionnaire_backstage.utils;

import com.alibaba.fastjson.JSONObject;
import com.ming.questionnaire_backstage.pojo.ResponseResult;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WebUtils
{
    /**
     * 将字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param string 待渲染的字符串
     * @return null
     */
    public static String renderString(HttpServletResponse response, String string) {
        try
        {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 将字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param string 待渲染的对象
     * @return null
     */
    public static String renderString(HttpServletResponse response, ResponseResult responseResult) {
        try
        {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(JSONObject.toJSONString(responseResult));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
