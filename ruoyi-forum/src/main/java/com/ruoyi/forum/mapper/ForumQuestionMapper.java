package com.ruoyi.forum.mapper;

import java.util.List;
import java.util.Map;

import com.ruoyi.cms.domain.Article;
import com.ruoyi.forum.domain.ForumQuestion;
import org.apache.ibatis.annotations.Param;

/**
 * 问题Mapper接口
 * 
 * @author ruoyi
 * @date 2021-03-12
 */
public interface ForumQuestionMapper 
{
    /**
     * 查询问题
     * 
     * @param id 问题ID
     * @return 问题
     */
    public ForumQuestion selectForumQuestionById(String id);
    /**
     * 查询内容
     * @param id
     * @return
     */
    public Map<String,Object> getQuestionContent(String id);
    /**
     * 查询问题列表
     * 
     * @param forumQuestion 问题
     * @return 问题集合
     */
    public List<ForumQuestion> selectForumQuestionList(ForumQuestion forumQuestion);

    /**
     * 新增问题
     * 
     * @param forumQuestion 问题
     * @return 结果
     */
    public int insertForumQuestion(ForumQuestion forumQuestion);

    /**
     * 插入内容
     * @param forumQuestion
     */
    public int insertQuestionContent(ForumQuestion forumQuestion);

    /**
     * 修改问题
     * 
     * @param forumQuestion 问题
     * @return 结果
     */
    public int updateForumQuestion(ForumQuestion forumQuestion);

    /**
     *  更新问题内容
     * @param forumQuestion
     * @return
     */
    public int updateQuestionContent(ForumQuestion forumQuestion);

    /**
     * 删除问题
     * 
     * @param id 问题ID
     * @return 结果
     */
    public int deleteForumQuestionById(String id);

    /**
     * 批量删除问题
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteForumQuestionByIds(String[] ids);


    /**
     * 查询收藏的问题列表
     *
     * @param forumQuestion 问题
     * @return 问题集合
     */
    public List<ForumQuestion> selectMyFavouriteList(ForumQuestion forumQuestion);

    /**
     * 取消收藏问题
     * @param yhid
     * @param question_id
     * @return
     */
    public int removeFavourite(@Param("yhid") String yhid, @Param("question_id") String question_id);

    /**
     * 收藏问题
     * @param yhid
     * @param question_id
     * @return
     */
    public int addFavourite(@Param("yhid") String yhid, @Param("question_id") String question_id);

    /**
     * 收藏状态(是否收藏)
     * @param yhid
     * @param question_id
     * @return
     */
    public Integer selectFavouriteFlag(@Param("yhid") String yhid,@Param("question_id") String question_id);

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
     * 查询Ta的动态
     * @return 问题集合
     */
    public List<ForumQuestion> selectHisQuestions(String yhid);

    /**
     * 查新用户帖子数量
     * @param userId
     * @return
     */
    public int selectQuestionCount(String userId);


    public int updateAvailable(ForumQuestion forumQuestion);

    public void batchPublish(String[] ids);

    public int questionTagCount(String tagId);

    public int deleteQuestionContentByIds(String[] ids);
}
