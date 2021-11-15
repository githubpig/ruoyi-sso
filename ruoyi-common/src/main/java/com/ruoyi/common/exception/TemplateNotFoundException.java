package com.ruoyi.common.exception;

/**
 * 不存在的模板异常
 * 
 * @author ruoyi
 */
public class TemplateNotFoundException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    protected final String message;

    public TemplateNotFoundException(String message)
    {
        this.message = message;
    }

    public TemplateNotFoundException(String message, Throwable e)
    {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage()
    {
        return message;
    }
}
