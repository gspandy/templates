package net.xicp.hkscript.gateway.controller;
import net.xicp.hkscript.gateway.gateway.ServiceGateway;

import com.alibaba.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@Controller 
@RequestMapping("/")
public class CommonController {

    @Autowired
    private ServiceGateway serviceGateway;

    @RequestMapping(value="/api/{group}/{service}/{method}", method = RequestMethod.GET)
    @ResponseBody
    public Object list(@PathVariable String group, @PathVariable String service, @PathVariable String method,
            @RequestHeader (value="environment",required=false) String enviroment) {
        Map ret = new HashMap();
        if(enviroment!=null){
            RpcContext.getContext().setAttachment("environment", enviroment);

        }
        Object data = serviceGateway.invokeWithoutArg(group, service, method);
        ret = this.processServiceResult(ret, data);
        
        RpcContext.getContext().setAttachment("environment", null);
        
        return ret;

    }

  

    
    @RequestMapping(value="/api/{group}/{service}/{method}", method = RequestMethod.POST)
    @ResponseBody
    public Object post(@PathVariable String group, @PathVariable String service, 
            @PathVariable String method,@RequestHeader (value="environment",required=false) String enviroment,HttpServletRequest request) {
        Map ret = new HashMap();
        if(enviroment!=null){
            RpcContext.getContext().setAttachment("environment", enviroment);

        }
        Object data = serviceGateway.invoke(group, service, method,request);
        
        ret = this.processServiceResult(ret, data);
        
        RpcContext.getContext().setAttachment("environment", null);
        return ret;
    }
    
    public Map processServiceResult(Map ret,Object data){
        if(data instanceof Map && ((Map)data).containsKey("success") && ((Map)data).containsKey("errorCode")){
            Boolean success = (Boolean) ((Map)data).get("success");
            Integer errorCode = (Integer) ((Map)data).get("errorCode");
            if(success){
                ret.put("error_code", 0);
                ret.put("data", ((Map)data).get("body"));
            }
            else{
                ret.put("error_code", errorCode);
                ret.put("error",  ((Map)data).get("message"));
            }
        }
        else{
            ret.put("error_code", 0);
            ret.put("data", data);
        }
        
        return ret;
        
    }
    

    

}



