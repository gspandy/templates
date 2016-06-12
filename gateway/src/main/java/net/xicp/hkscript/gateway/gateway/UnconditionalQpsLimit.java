package net.xicp.hkscript.gateway.gateway;

import java.util.Date;

public class UnconditionalQpsLimit implements ConditionalQPSLimit{
    
    
    private Long qpsLimit;

    public void setQpsLimit(Long qpsLimit) {
        this.qpsLimit = qpsLimit;
    }

    public boolean matches(Date date) {
        return true;
    }

    public Long getQpsLimit() {
        return qpsLimit;
    }

}
