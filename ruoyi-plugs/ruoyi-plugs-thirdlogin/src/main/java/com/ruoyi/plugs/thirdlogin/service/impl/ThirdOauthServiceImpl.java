package com.ruoyi.plugs.thirdlogin.service.impl;

import com.ruoyi.plugs.thirdlogin.domain.ThirdOauth;
import com.ruoyi.plugs.thirdlogin.mapper.ThirdOauthMapper;
import com.ruoyi.plugs.thirdlogin.service.IThirdOauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThirdOauthServiceImpl implements IThirdOauthService {

    @Autowired
    private ThirdOauthMapper thirdOauthMapper;

    @Override
    public List<ThirdOauth> selectThirdOauthList(ThirdOauth thirdOauth) {
        return thirdOauthMapper.selectThirdOauthList(thirdOauth);
    }

    @Override
    public int insertThirdOauth(ThirdOauth thirdOauth) {
        return thirdOauthMapper.insertThirdOauth(thirdOauth);
    }

    @Override
    public int updateThirdOauth(ThirdOauth thirdOauth) {
        return thirdOauthMapper.updateThirdOauth(thirdOauth);
    }

    @Override
    public int deleteThirdOauthByUserIdAndLoginType(String userId, String loginType) {
        return thirdOauthMapper.deleteThirdOauthByUserIdAndLoginType(userId,loginType);
    }
}
