package com.ruoyi.plugs.thirdlogin.mapper;

import com.ruoyi.plugs.thirdlogin.domain.ThirdOauth;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 第三方授权
 *
 * @author ruoyi
 * @date 2021-06-24
 */
public interface ThirdOauthMapper
{
    public List<ThirdOauth> selectThirdOauthList(ThirdOauth thirdOauth);

    public int insertThirdOauth(ThirdOauth thirdOauth);

    public int updateThirdOauth(ThirdOauth thirdOauth);

    public int deleteThirdOauthByUserIdAndLoginType(@Param("userId") String userId, @Param("loginType") String loginType);
}
