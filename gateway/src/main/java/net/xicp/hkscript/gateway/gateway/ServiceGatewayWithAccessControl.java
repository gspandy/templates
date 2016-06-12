package net.xicp.hkscript.gateway.gateway;

import javax.servlet.http.HttpServletRequest;



public class ServiceGatewayWithAccessControl implements ServiceGateway {
    
    
    private GatewayAcessControlConfiguration configuration;
    
    private ServiceGateway serviceGateway;

    public ServiceGateway getServiceGateway() {
        return serviceGateway;
    }

    public void setServiceGateway(ServiceGateway serviceGateway) {
        this.serviceGateway = serviceGateway;
    }

    public GatewayAcessControlConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(GatewayAcessControlConfiguration configuration) {
        this.configuration = configuration;
    }
    
    
    protected void controlAccess(String group, String service, String method){
//        GatewayServiceAuthentication methodAuthentication = this.configuration.getAuthentication(group, service,method);
//        if(methodAuthentication!=null)
//            methodAuthentication.trust(request, group, service, method);
//        
//        GatewayServiceAuthentication serviceAuthentication = this.configuration.getAuthentication(group, service);
//        if(serviceAuthentication!=null)
//            serviceAuthentication.trust(request, group, service, null);
        
        if(!this.configuration.allow(group, service,method))
            throw new AccessControlException(group+"/"+service+"/"+method,AccessControlException.DISALLOW);

        
        Counter serviceQPSCounter = this.configuration.getCurrentSecondQuestCounter(group, service);
        if(serviceQPSCounter!=null&&!serviceQPSCounter.canResponse())
            throw new AccessControlException(group+"/"+service+"/"+" exceed qps limit",AccessControlException.QPS_EXCEED_LIMIT);
        
        
        Counter methodQPSCounter = this.configuration.getCurrentSecondQuestCounter(group, service,method);
        if(methodQPSCounter!=null&&!methodQPSCounter.canResponse())
            throw new AccessControlException(group+"/"+service+"/"+method+" exceed qps limit",AccessControlException.QPS_EXCEED_LIMIT);
        
        
        Counter serviceGlobalQPSCounter = this.configuration.getGlobalCurrentSecondQuestCounter(group, service);
        if(serviceGlobalQPSCounter!=null&&!serviceGlobalQPSCounter.canResponse())
            throw new AccessControlException(group+"/"+service+"/"+" exceed global qps limit",AccessControlException.QPS_EXCEED_LIMIT);
        
        
        Counter methodGlobalQPSCounter = this.configuration.getGlobalCurrentSecondQuestCounter(group, service,method);
        if(methodGlobalQPSCounter!=null&&!methodGlobalQPSCounter.canResponse())
            throw new AccessControlException(group+"/"+service+"/"+method+" exceed global qps limit",AccessControlException.QPS_EXCEED_LIMIT);
            
        
 
    }
    
    

    public Object invoke(String group, String service, String method, HttpServletRequest request) {
        String realService = this.configuration.getServiceByAlias(group, service);
        if(realService==null){
            realService = service;
        }
        
        
        this.controlAccess(group, realService, method);


        String[] parameters = this.configuration.getMethodParametersDefination(group, realService, method);
        if(parameters!=null){
            return this.serviceGateway.invokeWithTypes(group, realService, method, request, parameters);
        }
        else{
            return this.serviceGateway.invoke(group, realService, method, request);
        }
        
    }

    public Object invoke(String group, String service, String method, String json) {
        String realService = this.configuration.getServiceByAlias(group, service);
        if(realService==null){
            realService = service;
        }
        
        
        
        this.controlAccess(group, realService, method);
        
        String[] parameters = this.configuration.getMethodParametersDefination(group, realService, method);
        if(parameters!=null){
            return this.serviceGateway.invokeWithTypes(group, realService, method, json, parameters);
        }
        else{
            return this.serviceGateway.invoke(group, realService, method, json);
        }
    }

    public Object invokeWithoutArg(String group, String service, String method) {
        String realService = this.configuration.getServiceByAlias(group, service);
        if(realService==null){
            realService = service;
        }
        
        
        this.controlAccess(group, realService, method);
        
        return this.serviceGateway.invokeWithoutArg(group, realService, method);
    }

    public Object invokeWithOneArg(String group, String service, String method, Object arg) {
        String realService = this.configuration.getServiceByAlias(group, service);
        if(realService==null){
            realService = service;
        }
        
        
        this.controlAccess(group, realService, method);
        
        
        return this.serviceGateway.invokeWithoutArg(group, realService, method);
    }

    public Object invokeWithTypes(String group, String service, String method, String json, String[] types) {
        String realService = this.configuration.getServiceByAlias(group, service);
        if(realService==null){
            realService = service;
        }
        
        
        this.controlAccess(group, realService, method);
        
        return this.serviceGateway.invokeWithTypes(group, realService, method, json, types);
    }

    public Object invokeWithTypes(String group, String service, String method, HttpServletRequest request,
            String[] types) {
        String realService = this.configuration.getServiceByAlias(group, service);
        if(realService==null){
            realService = service;
        }
        
        
        this.controlAccess(group, realService, method);
        
        return this.serviceGateway.invokeWithTypes(group, service, method, request, types);
    }

    public Object invokeWithTypes(String group, String service, String method, Object[] args, String[] types) {
        String realService = this.configuration.getServiceByAlias(group, service);
        if(realService==null){
            realService = service;
        }
        
        
        
        this.controlAccess(group, realService, method);
        
        return this.serviceGateway.invokeWithTypes(group, service, method, args, types);
    }

}
