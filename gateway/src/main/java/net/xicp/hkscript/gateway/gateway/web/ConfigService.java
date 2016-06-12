package net.xicp.hkscript.gateway.gateway.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;


@Service
public class ConfigService {
    private Logger logger = Logger.getLogger(this.getClass());
    

    @Value("${config.tableName:t_access_control_configuration}")
    private String configTableName;


    private BeanPropertyRowMapper<Config> configBeanRowMapper;

    
    public ConfigService(){
        this.configBeanRowMapper = new BeanPropertyRowMapper<Config>();
        configBeanRowMapper.setMappedClass(Config.class);
    }
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    
    public List<Config> list() {
        List<Config> configs = this.jdbcTemplate.query("select * from "+this.configTableName+" where is_deleted = 0", configBeanRowMapper);

        return configs;
    }
    
    public void remove(Integer id){
        this.jdbcTemplate.update("delete "+this.configTableName+" where id=?", id);
    }
    
    
    public void remove(List<Integer> ids){
        for(Integer id:ids)
            this.jdbcTemplate.update("delete from "+this.configTableName+" where id=?", id);
    }
    
    
    public void remove(String group,String service,String method,String parameter){
        this.jdbcTemplate.update("update "+this.configTableName+" set is_deleted = 1 where `group`=? and service = ? and method =? and parameter = ?", group,service,method,parameter);
    }
    
    
    public void remove(String group,String service,String parameter){
        this.jdbcTemplate.update("update "+this.configTableName+" set is_deleted = 1 where `group`=? and service = ? and  and parameter = ?  and (method is null or method='' or method='*') ", group,service,parameter);
    }
    
    
    public Config select(Integer id){
        return this.jdbcTemplate.queryForObject("select * from "+this.configTableName+" where id = ?", this.configBeanRowMapper, id);
    }
    
    
    public Config select(String group,String service,String parameter){
        List<Config> result = this.jdbcTemplate.query("select * from "+this.configTableName+" where `group` = ? and service = ? and parameter = ? and (method is null or method='' or method='*') and is_deleted=0 ", this.configBeanRowMapper, group,service,parameter);
        if(result.isEmpty())
            return null;
        else
            return result.get(0);
                    
    }
    
    
    public Config select(String group,String service,String method,String parameter){
        List<Config> result =  this.jdbcTemplate.query("select * from "+this.configTableName+" where `group` = ? and service = ? and parameter = ? and method = ? and is_deleted=0", this.configBeanRowMapper, group,service,parameter,method);
        if(result.isEmpty())
            return null;
        else
            return result.get(0);
    }
    
    
    public Config save(Config config){
        if(config.getId()!=null){
            this.jdbcTemplate.update("update "+this.configTableName+" set value=? where id=?",config.getValue(),config.getId());
        }
        else{
            Config oldConfig = null;
            if(config.getMethod()==null||config.getMethod().equals("*")||config.getMethod().equals("")){
                try{
                    oldConfig = this.select(config.getGroup(), config.getService(), config.getParameter());
                }
                catch(Exception e){
                    logger.error("errror when select:",e);
                }
            }
            else{
                try{
                    oldConfig = this.select(config.getGroup(), config.getService(), config.getMethod(),config.getParameter());
                }
                catch(Exception e){
                    logger.error("errror when select:",e);
                }
            }
            
            /*更新老数据*/
            if(oldConfig!=null){
                this.jdbcTemplate.update("update "+this.configTableName+" set value=? where id=?",config.getValue(),oldConfig.getId());
            }
            else{
                SimpleJdbcInsert insert = new SimpleJdbcInsert(this.jdbcTemplate).withTableName(this.configTableName)
                        .usingColumns("`group`", "service", "method", "parameter", "value", "is_deleted")
                        .usingGeneratedKeyColumns("id");
                Map args = new HashMap();
                args.put("`group`", config.getGroup());
                args.put("service", config.getService());
                args.put("method", config.getMethod());
                args.put("parameter", config.getParameter());
                args.put("value", config.getValue());
                args.put("is_deleted", 0);
                Number id = insert.execute(args);
                config.setId(id.intValue());
                
            }
        }
        
        return config;
        
        
    }
}
