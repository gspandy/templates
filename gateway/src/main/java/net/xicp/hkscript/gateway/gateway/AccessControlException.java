package net.xicp.hkscript.gateway.gateway;

public class AccessControlException extends RuntimeException{
    
    public static int DISALLOW = 5;
    
    public static int QPS_EXCEED_LIMIT = 6;
    

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int code;

    public AccessControlException(String message,int code) {
        super(message);
        this.code = code;
    }

    
    public AccessControlException(String message, int code,Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    
    public int getCode(){
        return this.code;
    }

    
}
