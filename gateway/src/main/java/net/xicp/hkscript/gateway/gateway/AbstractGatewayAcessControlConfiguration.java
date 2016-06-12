package net.xicp.hkscript.gateway.gateway;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public abstract class AbstractGatewayAcessControlConfiguration
        implements GatewayAcessControlConfiguration, ApplicationContextAware, InitializingBean {
 
    protected ApplicationContext context;

    protected ConcurrentMap<String, Object> configuration = new ConcurrentHashMap<String, Object>();
    

    protected ConcurrentMap<String, String> aliasMap = new ConcurrentHashMap<String, String>();

    protected final ConcurrentMap<String, MemoryCounter> counters = new ConcurrentHashMap<String, MemoryCounter>();

    protected Logger logger = Logger.getLogger(this.getClass());

    private boolean autoRefresh = false;
    
    protected boolean allowAll = false;

    
    protected JedisPool redisPool;

    public JedisPool getRedisPool() {
        return redisPool;
    }

    public void setRedisPool(JedisPool redisPool) {
        this.redisPool = redisPool;
    }

    public boolean isAutoRefresh() {
        return autoRefresh;
    }

    public void setAutoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;

    }



    protected GatewayServiceAuthentication getAuthentication(String key) {
        Object authenicationBean = configuration.get(key);
        if (authenicationBean != null) {
            if (authenicationBean instanceof GatewayServiceAuthentication) {
                return (GatewayServiceAuthentication) authenicationBean;
            } else {
                if (authenicationBean instanceof String) {
                    if (this.context != null)
                        try {
                            GatewayServiceAuthentication bean = this.context.getBean("authenicationBean",
                                    GatewayServiceAuthentication.class);
                            this.configuration.put(key, bean);
                            return bean;
                        } catch (Exception e) {
                            logger.warn("can't find bean named of " + authenicationBean, e);
                        }

                    try {
                        Class clazz = this.getClass().getClassLoader().loadClass((String) authenicationBean);
                        GatewayServiceAuthentication bean = (GatewayServiceAuthentication) clazz.newInstance();
                        this.configuration.put(key, bean);
                        return bean;
                    } catch (Exception e) {
                        logger.warn("can't find bean class named of " + authenicationBean, e);
                    }
                }

            }
        }
        return null;
    }

    public GatewayServiceAuthentication getAuthentication(String group, String service, String method) {
        String key = "authenication." + group + "." + service + "." + method;
        return this.getAuthentication(key);
    }

    public GatewayServiceAuthentication getAuthentication(String group, String service) {
        String key = "authenication." + group + "." + service;
        return this.getAuthentication(key);
    }
    
    

    
    public boolean allow(String group,String service,String method){
        if(allowAll==false){
            String key = "allow." + group + "." + service+"."+method;
            String value = (String) this.configuration.get(key);
            if(value!=null&&value.equals("ON")){
                return true;
            }
            else if(value!=null&&value.equals("OFF")){
                return false;
            }
            else{
                key = "allow." + group + "." + service;
                value = (String) this.configuration.get(key);
                if(value!=null&&value.equals("ON")){
                    return true;
                }
                return false;
            }
        }
        else
            return true;
    }

    public String[] getMethodParametersDefination(String group, String service, String method) {
        String key = "parameters." + group + "." + service + "." + method;
        try {
            String[] parameters = (String[]) this.configuration.get(key);
            return parameters;
        } catch (Exception e) {
            logger.warn("can't find parameters " + key, e);
        }

        return null;

    }

    public String getServiceByAlias(String group, String alias) {
        String key = group + "." + alias;
        try {
            String service = (String) this.aliasMap.get(key);
            return service;
        } catch (Exception e) {
            logger.warn("can't find service " + key, e);
        }

        return null;
    }

    public Counter getCurrentSecondQuestCounter(String group, String service, String method) {

        String key = "qps_limit." + group + "." + service + "." + method;
        return this.getCurrentSecondQuestCounter(key);
    }

    public Counter getCurrentSecondQuestCounter(String group, String service) {
        String key = "qps_limit." + group + "." + service;
        return this.getCurrentSecondQuestCounter(key);
    }

    protected Counter getCurrentSecondQuestCounter(String key) {
        Date currentDate = new Date();
        
        Long currentSecond = currentDate.getTime() / 1000;
        String counterKey = key + "." + currentSecond;
        
        MemoryCounter counter = this.counters.get(counterKey);

        if (counter == null) {
            List<ConditionalQPSLimit> dateConditions = 
                    (List<ConditionalQPSLimit>) this.configuration.get(key);
            ConditionalQPSLimit matchesDateCondition = null;
            
            if (dateConditions != null) {
                for (ConditionalQPSLimit dc : dateConditions) {
                    if (dc.matches(currentDate)) {
                        matchesDateCondition = dc;
                        break;
                    }
                }
                /*根据匹配条件创建counter*/
                if (matchesDateCondition != null) {
                    /* 锁住 */
                    counter = new MemoryCounter();
                    counter.setQpsLimit(matchesDateCondition.getQpsLimit());
                    /*同一时间，同一个key只能匹配上一个matchesDateCondition,锁住，避免同一时间多个生成多个counter*/
                    synchronized (matchesDateCondition) {
                        MemoryCounter tmpCounter = this.counters.get(counterKey);
                        if (tmpCounter == null) {
                            this.counters.put(counterKey, counter);
                        } else
                            counter = tmpCounter;
                    }

                }
            }
            
            long lastSecond = currentSecond - 4;
            /*清除垃圾数据*/
            while(lastSecond<currentSecond){
                Object o = this.counters.remove(key+"."+lastSecond);
                if(o!=null)
                    break;
                lastSecond ++;
            }

        }

        return counter;

    }

    protected abstract void loadConfiguration();

    public void afterPropertiesSet() throws Exception {
        this.loadConfiguration();
    }
    
    
    public Object getParameter(String group, String service, String method,String parameter) {
       
        return this.configuration.get(parameter+"."+group+"."+service+"."+method);
    }

    public Object getParameter(String group, String service,String parameter) {
        return this.configuration.get(parameter+"."+group+"."+service);
    }
    
    
    public Counter getGlobalCurrentSecondQuestCounter(String group, String service, String method) {
        String key = "g_qps_limit." + group + "." + service + "." + method;
        return this.getGlobalCurrentSecondQuestCounter(key);
    }

    public Counter getGlobalCurrentSecondQuestCounter(String group, String service) {
        String key = "g_qps_limit." + group + "." + service ;
        return this.getGlobalCurrentSecondQuestCounter(key);
    }
    
    
    protected Counter getGlobalCurrentSecondQuestCounter(String key) {
        Jedis jedis = this.redisPool!=null?this.redisPool.getResource():null;
        if(jedis==null){
            logger.warn("redis is not avaliable");
            return null;
        }
            
        
        Date currentDate = new Date();
        
        Long currentSecond = currentDate.getTime() / 1000;
        String counterKey = key + "." + currentSecond;
        try{
            String counterStr = jedis.get(counterKey);
            RedisCounter redisCounter = null;
            if(counterStr==null){
                List<ConditionalQPSLimit> dateConditions = 
                        (List<ConditionalQPSLimit>) this.configuration.get(key);
                ConditionalQPSLimit matchesDateCondition = null;
                
                if (dateConditions != null) {
                    for (ConditionalQPSLimit dc : dateConditions) {
                        if (dc.matches(currentDate)) {
                            matchesDateCondition = dc;
                            break;
                        }
                    }
                    /*根据匹配条件创建counter*/
                    if (matchesDateCondition != null) {
                        /* 锁住 */
                        
                        /*同一时间，同一个key只能匹配上一个matchesDateCondition,锁住，避免同一时间多个生成多个counter*/
                        synchronized (matchesDateCondition) {
                            String tmpCounter = jedis.get(counterKey);
                            if (tmpCounter == null) {
                                jedis.set(counterKey, String.valueOf(matchesDateCondition.getQpsLimit()));
                                /*4秒过期*/
                                jedis.expire(counterKey, 4);
                                redisCounter = new RedisCounter(this.redisPool,counterKey);
                            } else{
                                redisCounter = new RedisCounter(this.redisPool,counterKey);
                            }
                        }

                    }
                }
            }
            else{
                redisCounter = new RedisCounter(this.redisPool,counterKey);
            }
            
            
            
            return redisCounter;
            
        }
        catch(Exception e){
            logger.error("redis error",e);
        }
        finally{
            try{
                jedis.close();
                
            }
            catch(Exception e){
                logger.error(e);
            }
        }
        
        return null;
    }
    
    
    public boolean allowAll() {
       
        return this.allowAll;
    }

}
