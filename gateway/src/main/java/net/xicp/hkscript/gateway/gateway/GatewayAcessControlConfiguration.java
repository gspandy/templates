package net.xicp.hkscript.gateway.gateway;

public interface GatewayAcessControlConfiguration {

    
    
//    public GatewayServiceAuthentication getAuthentication(String group,String service,String method);
//    
//    
//    public GatewayServiceAuthentication getAuthentication(String group,String service);
    
    
    
    public String[] getMethodParametersDefination(String group,String service,String method);
    
    public String getServiceByAlias(String group,String alias);
    
    public Counter getCurrentSecondQuestCounter(String group,String service,String method);
    
    public Counter getCurrentSecondQuestCounter(String group,String service);
    
    
    public Counter getGlobalCurrentSecondQuestCounter(String group,String service,String method);
    
    public Counter getGlobalCurrentSecondQuestCounter(String group,String service);
    
    public Object getParameter(String group,String service,String method,String parameter);
    
    public Object getParameter(String group,String service,String parameter);
    
    public boolean allowAll();    
    
    public boolean allow(String group,String service,String method);
    
}
