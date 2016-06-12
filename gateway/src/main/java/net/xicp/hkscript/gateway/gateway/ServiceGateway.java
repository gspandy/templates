package net.xicp.hkscript.gateway.gateway;

import javax.servlet.http.HttpServletRequest;

public interface ServiceGateway {

    
    /**
     * 根据将http请求转换为对service名称,方法名的调用
     * @param service
     * @param method
     * @param request
     * @return
     * @throws ParameterParseException
     */
    public Object invoke(String group,String service,String method,HttpServletRequest request);
    
  
    /**
     * 
     * 根据将json字符串转化为参数，根据service名称,方法名进行调用
     * @param service
     * @param method
     * @param json
     * @return
     * @throws ParameterParseException
     */
    public Object invoke(String group,String service,String method,String json);
    
    
    public Object invokeWithoutArg(String group,String service,String method);
    
    public Object invokeWithOneArg(String group,String service,String method,Object arg);
    
    public Object invokeWithTypes(String group,String service,String method,String json,String[] types);
    
    
    public Object invokeWithTypes(String group,String service,String method,HttpServletRequest request,String[] types);
    
    public Object invokeWithTypes(String group,String service,String method,Object[] args,String[] types);
    
    

     
  
}
