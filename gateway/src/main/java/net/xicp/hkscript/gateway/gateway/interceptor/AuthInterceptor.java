package net.xicp.hkscript.gateway.gateway.interceptor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public class AuthInterceptor extends HandlerInterceptorAdapter{
    private Logger logger = Logger.getLogger(AuthInterceptor.class);

    
    public String getAuthServer() {
        return authServer;
    }

    public void setAuthServer(String authServer) {
        this.authServer = authServer;
    }

    public String getSsoServer() {
        return ssoServer;
    }

    public void setSsoServer(String ssoServer) {
        this.ssoServer = ssoServer;
    }

    public String getAuthTokenName() {
        return authTokenName;
    }

    public void setAuthTokenName(String authTokenName) {
        this.authTokenName = authTokenName;
    }

    public String getSystemKey() {
        return systemKey;
    }

    public void setSystemKey(String systemKey) {
        this.systemKey = systemKey;
    }

    public boolean isNative() {
        return isNative;
    }

    public void setNative(boolean isNative) {
        this.isNative = isNative;
    }


    private String authServer;

    
    private String ssoServer;

    
    private String authTokenName;

    
    private String systemKey;

    
    private boolean isNative;

    
    
    CloseableHttpClient httpClient = HttpClients.createDefault();
    
    RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(1000)
            .setConnectTimeout(1000).build();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isNative){
//            this.getBasicController(handler).userToken = "";
//            this.getBasicController(handler).loginUser = "native";
            return true;
        }else{
            //1.验证cookie
            Cookie cookie = validateCookie(request.getCookies());
            if (cookie == null || StringUtils.isEmpty(cookie.getValue())) {
                logger.debug("未登陆");
                response.sendRedirect(this.ssoServer + "/user/login?system_key=" + systemKey);
                return false;
            }
            //2.验证权限
            Map responseData = requestAuthCheckAccess(request.getContextPath(), cookie.getValue()); //请求auth系统
            Map mapUserInfo = null;
            if(responseData!=null){
                Integer ret = (Integer) responseData.get("ret");
                Map mapData = ret!=null&&ret==1? (Map) responseData.get("data"):null;

                Boolean access = false;
                if(mapData!=null){
                    mapUserInfo = (Map) mapData.get("user_info");
                    access = (Boolean) mapData.get("access");
                }
                
                if ( ret == null || ret != 1) {  //auth系统错误
                    logger.debug("权限系统出错,响应结果:" + responseData);
                    responseJSON(response, "500", "权限系统出错");
                    return false;
                }else if(mapUserInfo == null){  //登陆过期
                    logger.debug("登陆过期");
                    cleanCookie(response, cookie);
                    response.sendRedirect(ssoServer + "/user/login?system_key=" + systemKey);
                    return false;
                }else if(access==null||!access){  //无权限
                    logger.debug("无访问权限");
                    responseJSON(response, "401", "无访问权限");
                    return false;
                }
            }
            else{
                return false;
            }
//            this.getBasicController(handler).userToken = cookie.getValue();
//            this.getBasicController(handler).loginUser = (String) mapUserInfo.get("email");
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    //验证cookie是否有效，如果有效返回相应的cookie value，如果无效返回null
    private Cookie validateCookie(Cookie[] cookies) {
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (authTokenName!=null&&authTokenName.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }

    //清除cookie
    private void cleanCookie(HttpServletResponse response, Cookie cookie) {
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

//    private BasicController getBasicController(Object handler) {
//        if (handler instanceof HandlerMethod) {
//            return (BasicController) ((HandlerMethod) handler).getBean();
//        } else {
//            return (BasicController) handler;
//        }
//    }

    /*
     *请求auth系统并获取返回结果
     */
    private Map requestAuthCheckAccess(String entry, String token) {
        try {
            if("".equals(entry)){
                entry = "/";
            }
            
            if (StringUtils.isNotEmpty(token)) {
                Map<String, String> requestParam = new HashMap<>();
                requestParam.put("user_token", token);
                requestParam.put("system_key", systemKey);
                //requestParam.put("redirect_url", "http://172.18.4.33:8002/worker-manage");                
                requestParam.put("entry", entry);
                
                HttpPost httpPost = new HttpPost(authServer + "/adminuser/checkaccess");
                httpPost.setConfig(requestConfig);
                
                UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(builderBasicNameValuePairList(requestParam));
                httpPost.setEntity(encodedFormEntity);
                HttpResponse response = httpClient.execute(httpPost);   //发送请求并获取响应结果
                HttpEntity responseEntity = response.getEntity();
                String responseResult = EntityUtils.toString(responseEntity, "UTF-8");
              
             
                if (StringUtils.isNotEmpty(responseResult)) {
                    Map responseMap = JSON.parseObject(responseResult, Map.class);
                    return responseMap;

                }
            }
        } catch (Exception ex) {
            logger.debug("权限系统出错", ex);
        }
        return null;
    }

    
    private static List<BasicNameValuePair> builderBasicNameValuePairList(Map<String, String> requestParams) {
        if (requestParams != null && requestParams.size() > 0) {
            List<BasicNameValuePair> list = new ArrayList<>(requestParams.size());
            for (Map.Entry<String, String> entry : requestParams.entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            return list;
        }
        return null;
    }



    private void responseJSON(HttpServletResponse response, String errorCode, String data) throws IOException {

        JSONObject resultJson = new JSONObject();
        resultJson.put("error_code", errorCode);
        resultJson.put("data", data);
        String jsonString = resultJson.toJSONString();

        OutputStream stream = response.getOutputStream();
        stream.write(jsonString.getBytes("UTF-8"));
        stream.flush();
        stream.close();
    }


    private String getPageUri(HttpServletRequest request) {
        int pos = request.getRequestURI().lastIndexOf("/");
        String result = request.getRequestURI().substring(pos + 1, request.getRequestURI().length());
        String[] temps = result.split(";");
        return temps[0];
    }



}
