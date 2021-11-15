package com.ruoyi.framework.shiro;

import com.ruoyi.framework.shiro.token.MyUsernamePasswordToken;
import com.ruoyi.framework.shiro.token.MyUsernamePasswordToken;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;

public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {
    @Override
    public boolean doCredentialsMatch(AuthenticationToken authcToken, AuthenticationInfo info) {
        MyUsernamePasswordToken tk = (MyUsernamePasswordToken) authcToken;
        //如果是免密登录直接返回true
        if(tk.getLoginType().equals(MyUsernamePasswordToken.LoginType.NO_PWD.toString())){
            return true;
        }
        //不是免密登录，调用父类的方法
        return super.doCredentialsMatch(tk, info);
    }

}
