package com.guicedee.guicedinjection.exceptions;


public class ExcelRenderingException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public ExcelRenderingException()
    {
    }
    
    public ExcelRenderingException(String message)
    {
        super(message);
    }
    
    public ExcelRenderingException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public ExcelRenderingException(Throwable cause)
    {
        super(cause);
    }
    
    public ExcelRenderingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
