package com.ruoyi.forum.service;

import java.util.List;
import com.ruoyi.forum.domain.ForumQuestion;
import org.apache.ibatis.annotations.Param;

/**
 * 问题Service接口
 * 
 * @author ruoyi
 * @date 2021-03-12
 */
public interface IForumQuestionService 
{
    /**
     * 查询问题
     * 
     * @param id 问题ID
     * @return 问题
     */
    public ForumQuestion selectForumQuestionById(String id);

    /**
     * 查询问题列表
     * 
     * @param forumQuestion 问题
     * @return 问题集合
     */
    public List<ForumQuestion> selectForumQuestionList(ForumQuestion forumQuestion);

    /**
     * 查询问题列表(后台台使用)
     *
     * @param forumQuestion 问题
     * @return 问题集合
     */
    public List<ForumQuestion> selectForumQuestionList_Back(ForumQuestion forumQuestion);


    /**
     * 新增问题
     * 
     * @param forumQuestion 问题
     * @return 结果
     */
    public int insertForumQuestion(ForumQuestion forumQuestion);

    /**
     * 修改问题
     * 
     * @param forumQuestion 问题
     * @return 结果
     */
    public int updateForumQuestion(ForumQuestion forumQuestion);

    public int updateAvailable(ForumQuestion forumQuestion);
    /**
     * 批量删除问题
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteForumQuestionByIds(String ids);

    /**
     * 删除问题信息
     * 
     * @param id 问题ID
     * @return 结果
     */
    public int deleteForumQuestionById(String id);


    /**
     * 查询我的收藏列表
     *
     * @param forumQuestion 问题
     * @return 问题集合
     */
    public List<ForumQuestion> selectMyFavouriteList(ForumQuestion forumQuestion);

    /**
     * 批量删除问题
     * @param yhid
     * @param question_id
     * @return
     */
    public int removeFavourite(String yhid,String question_id);

    /**
     * 收藏问题
     * @param yhid
     * @param question_id
     * @return
     */
    public int addFavourite(String yhid,String question_id);

    /**
     * 收藏问题
     * @param yhid
     * @param question_id
     * @return
     */
    public Integer selectFavouriteFlag(String yhid,String question_id);

    /**
     * 点赞+1
     * @param id
     * @return
     */
    public int upVote(String id);

    /**
     * 帖子点击数+1
     * @param id
     * @return
     */
    public int questionLook(String id);


    /**
     * 置顶帖子
     * @param id
     * @return
     */
    public int setTop(String id);

    /**
     * 推荐
     * @param id
     * @return
     */
    public int setRecommend(String id);

    /**
     * 精品
     * @param id
     * @return
     */
    public int setGood(String id);

    /**
     * 查询最新的20个帖子
     * @return 问题集合
     */
    public List<ForumQuestion> selectNew20Question();

    /**
     * 查新用户帖子数量
     * @param userId
     * @return
     */
    public int selectQuestionCount(String userId);

    public void batchPublish(String[] ids);

    public int questionTagCount(String tagId);

}
