package com.ruoyi.forum.mapper;

import java.util.List;
import com.ruoyi.forum.domain.ForumComment;

/**
 * 评论管理Mapper接口
 * 
 * @author ruoyi
 * @date 2021-03-15
 */
public interface ForumCommentMapper 
{
    /**
     * 查询评论管理
     * 
     * @param id 评论管理ID
     * @return 评论管理
     */
    public ForumComment selectForumCommentById(Long id);

    /**
     * 查询评论管理列表
     * 
     * @param forumComment 评论管理
     * @return 评论管理集合
     */
    public List<ForumComment> selectForumCommentList(ForumComment forumComment);

    /**
     * 新增评论管理
     * 
     * @param forumComment 评论管理
     * @return 结果
     */
    public int insertForumComment(ForumComment forumComment);

    /**
     * 修改评论管理
     * 
     * @param forumComment 评论管理
     * @return 结果
     */
    public int updateForumComment(ForumComment forumComment);

    /**
     * 删除评论管理
     * 
     * @param id 评论管理ID
     * @return 结果
     */
    public int deleteForumCommentById(Long id);

    /**
     * 批量删除评论管理
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteForumCommentByIds(String[] ids);

    /**
     * 查询评论数
     *
     * @param tid 评论对象的id
     * @return 结果
     */
    public int selectCommentCount(String tid);

    /**
     * 评论点赞+1
     * @param id
     * @return
     */
    public int upVote(String id);

}
