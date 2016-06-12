package net.xicp.hkscript.gateway.gateway;

import java.util.Date;

public interface ConditionalQPSLimit {
    
    public boolean matches(Date date);
    
    public Long getQpsLimit(); 
}
