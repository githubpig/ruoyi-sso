package com.ruoyi.forum.service;

import com.ruoyi.forum.domain.ForumQuestion;
import com.ruoyi.forum.domain.ForumUser;
import com.ruoyi.forum.domain.ForumUserSign;
import com.ruoyi.forum.mapper.ForumQuestionMapper;
import com.ruoyi.forum.mapper.ForumUserMapper;
import com.ruoyi.system.domain.SysUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumUserService {
    @Autowired
    ForumUserMapper forumUserMapper;
    @Autowired
    ForumQuestionMapper forumQuestionMapper;

    public ForumUser selectByUserId(Long userId){
        return  forumUserMapper.selectByUserId(userId);
    }
    //插入新行，只有userId，其它为空
    public int insertNewForumUser(int userId){
        return forumUserMapper.insertNewForumUser(userId);
    }
    public int updateUserInfo(ForumUser user){
        return  forumUserMapper.updateUserInfo(user);
    }


    public ForumUserSign selectUserSign(Integer userId){
        return forumUserMapper.selectUserSign(userId);
    }

    public int insertUserSign(ForumUserSign forumUserSign){
        return forumUserMapper.insertUserSign(forumUserSign);
    }

    public int updateUserSign(ForumUserSign forumUserSign){
        return forumUserMapper.updateUserSign(forumUserSign);
    }

    public List<ForumUser> selectMyFocusList(String yhid){
        return forumUserMapper.selectMyFocusList(yhid);
    }

    public int addFocus(String yhid,String focus_user_id){
        return forumUserMapper.addFocus(yhid,focus_user_id);
    }

    public int removeFocus(String yhid,String focus_user_id){
        return forumUserMapper.removeFocus(yhid,focus_user_id);
    }

    public int selectFocusFlag(String yhid,String focus_user_id){
        return forumUserMapper.selectFocusFlag(yhid,focus_user_id);
    }

    public List<ForumQuestion> selectHisQuestions(String yhid){
        return forumQuestionMapper.selectHisQuestions(yhid);
    }

    public List<ForumUser> selectMyFansList(String yhid){
        return forumUserMapper.selectMyFansList(yhid);
    }

    public int selectFansCount(String userId){
        return forumUserMapper.selectFansCount(userId);
    }
    public int addVisitUser(String userId,String visit_user_id){
        return forumUserMapper.addVisitUser(userId,visit_user_id);
    }
    public int removeVisitUser(String userId,String visit_user_id){
        return forumUserMapper.removeVisitUser(userId,visit_user_id);
    }
    public List<ForumUser> selectVisitUsers(String userId){
        return forumUserMapper.selectVisitUsers(userId);
    }
    public List<SysUser> selectActivityUsers(){
        return forumUserMapper.selectActivityUsers();
    }

    public int insertDownloadHis(String userId,String url){
        return forumUserMapper.insertDownloadHis(userId,url);
    }

    public int selectDownloadHisCount(String userId,String url){
        return forumUserMapper.selectDownloadHisCount(userId,url);
    }
}
