package com.ruoyi.forum.service.impl;

import java.util.List;
import java.util.ArrayList;
import com.ruoyi.common.core.domain.Ztree;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.forum.mapper.ForumCommentMapper;
import com.ruoyi.forum.domain.ForumComment;
import com.ruoyi.forum.service.IForumCommentService;
import com.ruoyi.common.core.text.Convert;

/**
 * 评论管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-03-15
 */
@Service
public class ForumCommentServiceImpl implements IForumCommentService 
{
    @Autowired
    private ForumCommentMapper forumCommentMapper;

    /**
     * 查询评论管理
     * 
     * @param id 评论管理ID
     * @return 评论管理
     */
    @Override
    public ForumComment selectForumCommentById(Long id)
    {
        return forumCommentMapper.selectForumCommentById(id);
    }

    /**
     * 查询评论管理列表
     * 
     * @param forumComment 评论管理
     * @return 评论管理
     */
    @Override
    public List<ForumComment> selectForumCommentList(ForumComment forumComment)
    {
        return forumCommentMapper.selectForumCommentList(forumComment);
    }
    /**
     * 查询评论管理列表(后台)
     *
     * @param forumComment 评论管理
     * @return 评论管理
     */
    @Override
    public List<ForumComment> selectForumCommentList_Back(ForumComment forumComment)
    {
        SysUser user= ShiroUtils.getSysUser();
        if(!user.isAdmin()){
            forumComment.setUserId(user.getUserId().toString());
        }
        return forumCommentMapper.selectForumCommentList(forumComment);
    }
    /**
     * 新增评论管理
     * 
     * @param forumComment 评论管理
     * @return 结果
     */
    @Override
    public int insertForumComment(ForumComment forumComment)
    {
        forumComment.setCreateTime(DateUtils.getNowDate());
        return forumCommentMapper.insertForumComment(forumComment);
    }

    /**
     * 修改评论管理
     * 
     * @param forumComment 评论管理
     * @return 结果
     */
    @Override
    public int updateForumComment(ForumComment forumComment)
    {
        return forumCommentMapper.updateForumComment(forumComment);
    }

    /**
     * 删除评论管理对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteForumCommentByIds(String ids)
    {
        return forumCommentMapper.deleteForumCommentByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除评论管理信息
     * 
     * @param id 评论管理ID
     * @return 结果
     */
    @Override
    public int deleteForumCommentById(Long id)
    {
        return forumCommentMapper.deleteForumCommentById(id);
    }

    /**
     * 查询评论管理树列表
     * 
     * @return 所有评论管理信息
     */
    @Override
    public List<Ztree> selectForumCommentTree()
    {
        List<ForumComment> forumCommentList = forumCommentMapper.selectForumCommentList(new ForumComment());
        List<Ztree> ztrees = new ArrayList<Ztree>();
        for (ForumComment forumComment : forumCommentList)
        {
            Ztree ztree = new Ztree();
            ztree.setId(forumComment.getId());
            ztree.setpId(forumComment.getPid());
            ztree.setName(forumComment.getTid());
            ztree.setTitle(forumComment.getTid());
            ztrees.add(ztree);
        }
        return ztrees;
    }

    @Override
    public int selectCommentCount(String tid) {
        return forumCommentMapper.selectCommentCount(tid);
    }

    @Override
    public int upVote(String id) {
        return forumCommentMapper.upVote(id);
    }
}
