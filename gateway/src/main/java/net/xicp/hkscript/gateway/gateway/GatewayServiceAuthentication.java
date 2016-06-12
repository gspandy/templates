package net.xicp.hkscript.gateway.gateway;

import javax.servlet.http.HttpServletRequest;

public interface GatewayServiceAuthentication {

    /**
     * 对request进行权限验证的接口
     * 
     * @param request
     * @param group
     * @param service
     * @param method
     * @throws AccessControlException
     */
    public void trust(HttpServletRequest request,String group,String service,String method) throws AccessControlException;
    
}
