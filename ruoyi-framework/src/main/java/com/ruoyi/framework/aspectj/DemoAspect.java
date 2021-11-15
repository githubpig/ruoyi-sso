package com.ruoyi.framework.aspectj;


import com.ruoyi.common.config.Global;
import com.ruoyi.common.exception.DemoModeException;
import com.ruoyi.common.utils.TimeUuidUtil;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.system.domain.CostTime;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 演示模式禁止增删改
 * @author lws
 *
 */
@Aspect
@Component
    public class DemoAspect {


    // 配置织入点
	//第一个*代表任意返回值；controller..代表controller包下及子包下；第二个*代表所有类；*(..)代表任意参数的所有方法
    @Pointcut("execution(* com.ruoyi..controller..*.addSave(..))||execution(* com.ruoyi..controller..*.editSave(..))||execution(* com.ruoyi..controller..*.remove*(..))")
    public void webLog(){}
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        if(Boolean.valueOf(Global.isDemoEnabled())){
            throw new DemoModeException();
        }
    }

}
