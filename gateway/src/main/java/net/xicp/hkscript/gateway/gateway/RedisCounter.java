package net.xicp.hkscript.gateway.gateway;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisCounter implements Counter{
    
    private JedisPool redisPool = null;
    
    private String key = null;
    
    private static Logger logger = Logger.getLogger(RedisCounter.class);
      
    public RedisCounter(JedisPool redisPool,String key) {
      
        this.redisPool = redisPool;
        this.key = key;
        
    }



    
    public boolean canResponse(){
        Jedis jedis = this.redisPool.getResource();
        try{
            return jedis.decr(key)>=0;
        }
        catch(Exception e){
            logger.error(e);
        }
        finally{
            if(jedis!=null)
                try{
                    jedis.close();
                    
                }
                catch(Exception e){
                    logger.error(e);
                }
        }   
        
        return false;
    }
}
