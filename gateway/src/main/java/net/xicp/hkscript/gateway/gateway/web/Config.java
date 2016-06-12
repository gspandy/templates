package net.xicp.hkscript.gateway.gateway.web;

public class Config {

    private Integer id;
    
    private String group;
    
    private String service;
    
    private String method;
    
    private String parameter;
    
    private String value;
    
    private int isDeleted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        if(group!=null)
            this.group = group.trim();
        else
            this.group = null;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        if(service!=null)
            this.service = service.trim();
        else
            this.service = null;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        if(method!=null)
            this.method = method.trim();
        else
            this.method = null;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        if(parameter!=null)
            this.parameter = parameter.trim();
        else
            this.parameter = null;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if(value!=null)
            this.value = value.trim();
        else
            this.value = null;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}
