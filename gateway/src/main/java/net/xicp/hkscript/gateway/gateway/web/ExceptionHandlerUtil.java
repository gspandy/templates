package net.xicp.hkscript.gateway.gateway.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import net.xicp.hkscript.gateway.gateway.AccessControlException;
import net.xicp.hkscript.gateway.gateway.InvokeException;
import net.xicp.hkscript.gateway.gateway.ParameterParseException;
import net.xicp.hkscript.gateway.gateway.web.ExceptionHandlerUtil;

import net.xicp.hkscript.gateway.gateway.AccessControlException;
import net.xicp.hkscript.gateway.gateway.InvokeException;
import net.xicp.hkscript.gateway.gateway.ParameterParseException;


/**
 * Controller Advice
 * 
 * @author panzhiqi
 * 
 */
@ControllerAdvice
public class ExceptionHandlerUtil {
    private Logger logger = Logger.getLogger(ExceptionHandlerUtil.class);
    
    public static int INVOKE_ERROR = 1003;
    public static int DISALLOW_ERROR = 1005;
    public static int QPS_EXCEED_LIMIT_ERROR = 1006;
    public static int PARAMETER_ERROR = 1002;
    public static int SYSTEM_ERROR  = 1001;

    @ExceptionHandler(InvokeException.class)
    @ResponseBody
    public Map handleInvokeException(HttpServletRequest req, Exception ex) {
        Map ret = new HashMap();
        logger.error("invoke error",ex);
        ret.put("error_code", INVOKE_ERROR);
        return ret;
    }

    @ExceptionHandler(AccessControlException.class)
    @ResponseBody
    public Map handleAccessControl(HttpServletRequest req, AccessControlException ex) {
        Map ret = new HashMap();
        logger.error("access error",ex);
        ret.put("error_code", ex.getCode()+1000);
        return ret;

    }

    @ExceptionHandler(ParameterParseException.class)
    @ResponseBody
    public Map handleInvalidParam(HttpServletRequest req, Exception ex) {
        Map ret = new HashMap();
        logger.error("parameter error",ex);
        ret.put("error_code", PARAMETER_ERROR);
        return ret;
    }

    
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public Map handleSystemException(HttpServletRequest req, Throwable ex) {
        Map ret = new HashMap();
        logger.error("system error",ex);
        ret.put("error_code", SYSTEM_ERROR);
        ret.put("message",ex.getMessage());
        return ret;
    }

    
}
