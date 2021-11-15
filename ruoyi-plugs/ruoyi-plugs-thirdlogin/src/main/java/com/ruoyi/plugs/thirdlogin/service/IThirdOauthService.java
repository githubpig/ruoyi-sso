package com.ruoyi.plugs.thirdlogin.service;

import com.ruoyi.plugs.thirdlogin.domain.ThirdOauth;

import java.util.List;

public interface IThirdOauthService {

    public List<ThirdOauth> selectThirdOauthList(ThirdOauth thirdOauth);

    public int insertThirdOauth(ThirdOauth thirdOauth);

    public int updateThirdOauth(ThirdOauth thirdOauth);

    public int deleteThirdOauthByUserIdAndLoginType(String userId, String loginType);
}
