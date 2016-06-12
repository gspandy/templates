package net.xicp.hkscript.gateway.gateway;

import java.util.concurrent.atomic.AtomicLong;

public class MemoryCounter implements Counter {
    
    private AtomicLong qps = new AtomicLong(0);
    
    private long qpsLimit;

    public void setQpsLimit(long qpsLimit) {
        this.qpsLimit = qpsLimit;
    }

    public long getQpsLimit(){
        return qps.get();
    }
      
    public long get(){
        return qps.get();
    }
    
    public boolean canResponse(){
        return qps.getAndIncrement()<this.qpsLimit;
    }
}
