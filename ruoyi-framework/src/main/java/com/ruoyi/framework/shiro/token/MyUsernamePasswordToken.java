package com.ruoyi.framework.shiro.token;

public class MyUsernamePasswordToken extends org.apache.shiro.authc.UsernamePasswordToken {

    private String loginType;
    public enum LoginType{
        PWD,NO_PWD
    }
    public MyUsernamePasswordToken(String username, String password, boolean rememberMe) {
        super(username, password, rememberMe, null);
        this.loginType = LoginType.PWD.toString();
    }
    public MyUsernamePasswordToken(String username) {
        super(username, "", false, null);
        this.loginType = LoginType.NO_PWD.toString();
    }
    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
}
