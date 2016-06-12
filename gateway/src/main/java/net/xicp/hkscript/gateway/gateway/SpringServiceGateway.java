package net.xicp.hkscript.gateway.gateway;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.alibaba.dubbo.rpc.service.GenericService;

public class SpringServiceGateway extends AbstractServiceGateway implements ApplicationContextAware {

    private ApplicationContext context;

    private static final ConcurrentMap<String, GenericService> SERVICE_CACHE = new ConcurrentHashMap<String, GenericService>();
    
    private int timeout = 50000;
    
    private int retries = 0;

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;

    }

    protected Object internalInvoke(String group, String service, String method, Object[] args,String[] types) {
        String signature = group + "/" + service ;
        
//        + "." + method;
//        if(types!=null&&types.length>0){
//            signature = signature +"/"+ StringUtils.join(types);
//        }
        
        GenericService genericService = this.SERVICE_CACHE.get(signature);
         
        try{
            if(genericService!=null){
                return genericService.$invoke(method, types, args);
            }
            else{
                ApplicationConfig appConfig = this.context.getBean(ApplicationConfig.class);
                ReferenceBean<GenericService> reference =new ReferenceBean<GenericService>();// 
                reference.setApplication(appConfig);
                reference.setApplicationContext(this.context);
                reference.setInterface(service);// 弱类型接口名
                //reference.setVersion("1.0.0");
                reference.setGeneric(true);// 声明为泛化接口 
                
                
                reference.setGroup(group);
    
                //reference.setUrl("dubbo://192.168.133.127:20887?scope=remote&validation=true");
                reference.setOwner("programmer");
                reference.afterPropertiesSet();
                
                reference.setRetries(this.retries);
                if (timeout > 0) {
                    reference.setTimeout(timeout);
                }
                
                genericService = reference.get();
                this.SERVICE_CACHE.put(signature, genericService);
                return genericService.$invoke(method, types, args);
            }
        }
        catch(Exception e){
            logger.error("error when inoke "+signature,e);
            throw new InvokeException("error when inoke "+signature,e);
        }

    }
    
    
    @Override
    protected Object internalInvoke(String group, String service, String method, Parameter[] params) {
        boolean typesIsNoNeed = false;
        String[] types =  null;
        Object[] args = null;
        if (params != null) {
            types =  new String[params.length];
            args = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                if(params[i].type==null){
                    typesIsNoNeed = true;
                }
                else{
                    types[i] = params[i].type;
                }
                
                args[i] = params[i].value;
            }
        }
        
        if(typesIsNoNeed){
            types = null;
        }

        return this.internalInvoke(group, service, method, args,types);
    }







}
