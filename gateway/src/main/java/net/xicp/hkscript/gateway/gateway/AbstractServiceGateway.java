package net.xicp.hkscript.gateway.gateway;


import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


public abstract class AbstractServiceGateway implements ServiceGateway {

    protected ObjectMapper mapper = new ObjectMapper();

    public AbstractServiceGateway() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
    }

    protected Logger logger = Logger.getLogger(this.getClass());

    public Object invoke(String group, String service, String method, HttpServletRequest request) {
        Object o = null;
        if (request != null &&  !request.getMethod().equalsIgnoreCase("get")) {
            try {
                o = this.mapper.readValue(request.getInputStream(), Object.class);
            } catch (Exception e) {
                logger.error("error when json read", e);
                throw new ParameterParseException("error when json read", e);
            }
        }
        
        if(o==null){
            return this.internalInvoke(group, service, method, null);
        }
        else{
            return this.internalInvoke(group, service, method, this.readParameters(o));
        }
        

    }
    
    
    public Object invoke(String group, String service, String method, String json) {
        Object o = null;
        if (json!=null && !"".equals(json)) {
            try {
                o = this.mapper.readValue(json, Object.class);
            } catch (Exception e) {
                logger.error("error when json read", e);
                throw new ParameterParseException("error when json read", e);
            }
        }
        
        if(o==null){
            return this.internalInvoke(group, service, method, null);
        }
        else{
            return this.internalInvoke(group, service, method, this.readParameters(o));
        }
        

    }
    
    protected abstract Object internalInvoke(String group, String service, String method, Parameter[] params);
    
    protected abstract Object internalInvoke(String group, String service, String method, Object[] args,String[] types) ;
    
    protected Parameter[] readParameters(Object o){
        if(o instanceof Map){
            Parameter[] p = new  Parameter[1];
            p[0] = new Parameter();
            String clazz  = (String) ((Map)o).get("@class");
            p[0].type = clazz;
            p[0].value = o;
            return p;
        }
        else{
            return this.readParameters((List)o);
        }
    }
    
    protected Parameter[] readParameters(List parameters){
        Parameter[] ret = new Parameter[parameters.size()];
        int i = 0;
        for(Object param:parameters){           
            Parameter p = new Parameter();
//            i++;
            if(param instanceof Map){
                String clazz  = (String) ((Map)param).get("@class");
                p.type = clazz;
                p.value = param;
            }
            else{
                //p.type = param.getClass().getName();
                p.value = param;
            }
            ret[i] = p;
            i++;
        }
        
        return ret;
        
    }

    protected static class Parameter {
        String type;
        Object value;
    }
    
    
    public Object invokeWithoutArg(String group, String service, String method) {
        return this.internalInvoke(group, service, method, new Object[0],new String[0]);
    }

    public Object invokeWithOneArg(String group, String service, String method, Object o) {
        Parameter p = new Parameter();
        p.value = o;
        p.type = null;
        return this.internalInvoke(group, service, method, new Parameter[]{p});
    }
    
    
    public Object invokeWithTypes(String group, String service, String method, String json, String[] types) {
        Object o = null;
        if (json!=null && !"".equals(json)) {
            try {
                o = this.mapper.readValue(json, Object.class);
            } catch (Exception e) {
                logger.error("error when json read", e);
                throw new ParameterParseException("error when json read", e);
            }
        }
        else{
            throw new ParameterParseException("json is null");
        }
        
        if(o instanceof List){
            
            if(((List)o).size()!=types.length){
                throw new ParameterParseException("args.size()!=types.length");
            }
            else{
                Object[] args = new Object[((List)o).size()];
                for(int i=0;i<args.length;i++){
                    args[i] = ((List)o).get(i);
                }
                return this.internalInvoke(group, service, method, args, types);
            }
        }
        else{
            throw new ParameterParseException("expect json is array");
        }
    }

    public Object invokeWithTypes(String group, String service, String method, HttpServletRequest request,
            String[] types) {
        Object o = null;
        if (request != null &&  !request.getMethod().equalsIgnoreCase("get")) {
            try {
                o = this.mapper.readValue(request.getInputStream(), Object.class);
            } catch (Exception e) {
                logger.error("error when json read", e);
                throw new ParameterParseException("error when json read", e);
            }
        }
        else{
            throw new ParameterParseException("request is get or null");
        }
        
        
        if(o instanceof List){
            
            if(((List)o).size()!=types.length){
                throw new ParameterParseException("args.size()!=types.length");
            }
            else{
                Object[] args = new Object[((List)o).size()];
                for(int i=0;i<args.length;i++){
                    args[i] = ((List)o).get(i);
                }
                return this.internalInvoke(group, service, method, args, types);
            }
        }
        else{
            throw new ParameterParseException("expect json is array");
        }
    }

    public Object invokeWithTypes(String group, String service, String method, Object[] args, String[] types) {
        return this.internalInvoke(group, service, method, args, types);
    }
}
