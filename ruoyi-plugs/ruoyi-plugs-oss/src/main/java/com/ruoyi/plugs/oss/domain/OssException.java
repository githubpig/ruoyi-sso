package com.ruoyi.plugs.oss.domain;

/**
 * OSS信息异常类
 *
 * @author zmr
 */
public class OssException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public OssException(String msg)
    {
        super(msg);
    }
}
