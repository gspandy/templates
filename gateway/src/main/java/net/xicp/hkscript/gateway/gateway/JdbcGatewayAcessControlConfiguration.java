package net.xicp.hkscript.gateway.gateway;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcGatewayAcessControlConfiguration extends AbstractGatewayAcessControlConfiguration implements Runnable{
    
    private DataSource dataSource;
    
    private String tableName = "t_access_control_configuration";

    private JdbcTemplate jdbcTemplate;
    
    private ConditionalQPSLimitParser qpsLimitParser = new ConditionalQPSLimitParser();
    
    private int refreshPeriod = 60;

    public int getRefreshPeriod() {
        return refreshPeriod;
    }

    public void setRefreshPeriod(int refreshPeriod) {
        this.refreshPeriod = refreshPeriod;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate =  new JdbcTemplate(dataSource);
    }
    
    private ScheduledThreadPoolExecutor scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

    @Override
    protected void loadConfiguration() {
        
        this.loadConfigurationFromDatabase();
        if(this.isAutoRefresh()){
            scheduledExecutorService.schedule(this, this.refreshPeriod, TimeUnit.SECONDS);
        }
    }
    
    protected void loadConfigurationFromDatabase(){
        List<Map<String, Object>> configurations = this.jdbcTemplate.queryForList("select * from "+this.tableName+" where is_deleted=0");
        ConcurrentMap<String, Object> tmpConfiguration = new ConcurrentHashMap<String, Object>();

        ConcurrentMap<String, String> tmpAliasMap = new ConcurrentHashMap<String, String>();
        
        for(Map<String,Object> configuration:configurations){
            String group = (String) configuration.get("group");
            if(group!=null){
                group = group.trim();
            }
            String service = (String) configuration.get("service");
            if(service!=null){
                service = service.trim();
            }
            String method = (String) configuration.get("method");
            if(method!=null){
                method = method.trim();
            }
            String parameter = (String) configuration.get("parameter");
            if(parameter!=null){
                parameter = parameter.trim();
            }
            String value = (String) configuration.get("value");
            if(value!=null){
                value = value.trim();
            }
            String key = parameter+ "." + group + "." + service;
            if(method!=null&&!method.trim().equals("*")&&!method.trim().equals("")){
                key = key+"."+method.trim();

            }
            
            
            if(parameter.equals("parameters")){
                String[] s = value.split(",");
                tmpConfiguration.put(key, s);
            }
            else if(parameter.equals("qps_limit")){
                List<ConditionalQPSLimit> limits = qpsLimitParser.parse(value);
                tmpConfiguration.put(key, limits);

            }
            else if(parameter.equals("g_qps_limit")){
                List<ConditionalQPSLimit> limits = qpsLimitParser.parse(value);
                tmpConfiguration.put(key, limits);

            }
            else if(parameter.equals("alias")){
                tmpAliasMap.put(group+"."+value, service);
                tmpConfiguration.put(key, value);
            }
            else if(parameter.equals("allow")){
                tmpConfiguration.put(key, value.toUpperCase());
            }
            else if(parameter.equals("allowAll")){
                if(value.equalsIgnoreCase("ON")){
                    this.allowAll = true;
                }
            }
            else{
                tmpConfiguration.put(key, value);
            }

        }
        
        this.configuration = tmpConfiguration;
        this.aliasMap = tmpAliasMap;
        

    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void run() {
        try{
            this.loadConfigurationFromDatabase();
        }
        catch(Exception e){
            this.logger.warn("load configuration error",e);
        }
        
        if(this.isAutoRefresh()){
            scheduledExecutorService.schedule(this, this.refreshPeriod, TimeUnit.SECONDS);
        }
        
    }







}
