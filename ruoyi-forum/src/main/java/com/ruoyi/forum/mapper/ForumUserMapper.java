package com.ruoyi.forum.mapper;


import com.ruoyi.forum.domain.ForumUser;
import com.ruoyi.forum.domain.ForumUserSign;
import com.ruoyi.system.domain.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ForumUserMapper {
    public ForumUser selectByUserId(Long userId);

    public int updateUserInfo(ForumUser user);

    public ForumUserSign selectUserSign(Integer userId);

    public int insertUserSign(ForumUserSign forumUserSign);

    public int updateUserSign(ForumUserSign forumUserSign);

    public List<ForumUser> selectMyFocusList(String yhid);
    public List<ForumUser> selectMyFansList(String yhid);

    public int addFocus(@Param("yhid")String yhid,@Param("focus_user_id")String focus_user_id);

    public int removeFocus(@Param("yhid")String yhid,@Param("focus_user_id")String focus_user_id);

    public int selectFocusFlag(@Param("yhid")String yhid,@Param("focus_user_id")String focus_user_id);

    public int insertNewForumUser(int userId);//插入新行，只有userId，其它为空

    public int selectFansCount(String userId);

    //增加访客记录
    public int addVisitUser(@Param("userId")String userId,@Param("visit_user_id")String visit_user_id);
    public int removeVisitUser(@Param("userId")String userId,@Param("visit_user_id")String visit_user_id);
    public List<ForumUser> selectVisitUsers(String userId);

    //查询活跃用户
    public List<SysUser> selectActivityUsers();

    public int insertDownloadHis(@Param("userId")String userId,@Param("url")String url);

    public int selectDownloadHisCount(@Param("userId")String userId,@Param("url")String url);
}
