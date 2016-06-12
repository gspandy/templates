package net.xicp.hkscript.gateway.gateway;

public class ParameterParseException extends RuntimeException{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ParameterParseException(String message) {
        super(message);
    }

    
    public ParameterParseException(String message, Throwable cause) {
        super(message, cause);
    }

    
}
