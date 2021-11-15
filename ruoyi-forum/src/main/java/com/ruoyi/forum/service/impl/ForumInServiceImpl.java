package com.ruoyi.forum.service.impl;

import java.util.List;

import com.ruoyi.common.utils.Guid;
import com.ruoyi.framework.util.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.forum.mapper.ForumInMapper;
import com.ruoyi.forum.domain.ForumIn;
import com.ruoyi.forum.service.IForumInService;
import com.ruoyi.common.core.text.Convert;

/**
 * 入账记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-03-19
 */
@Service
public class ForumInServiceImpl implements IForumInService 
{
    @Autowired
    private ForumInMapper forumInMapper;

    /**
     * 查询入账记录
     * 
     * @param id 入账记录ID
     * @return 入账记录
     */
    @Override
    public ForumIn selectForumInById(String id)
    {
        return forumInMapper.selectForumInById(id);
    }

    /**
     * 查询入账记录列表
     * 
     * @param forumIn 入账记录
     * @return 入账记录
     */
    @Override
    public List<ForumIn> selectForumInList(ForumIn forumIn)
    {
        forumIn.setUserId(ShiroUtils.getUserId().toString());
        return forumInMapper.selectForumInList(forumIn);
    }

    /**
     * 新增入账记录
     * 
     * @param forumIn 入账记录
     * @return 结果
     */
    @Override
    public int insertForumIn(ForumIn forumIn)
    {
        forumIn.setId(Guid.get());

        return forumInMapper.insertForumIn(forumIn);
    }

    /**
     * 修改入账记录
     * 
     * @param forumIn 入账记录
     * @return 结果
     */
    @Override
    public int updateForumIn(ForumIn forumIn)
    {
        return forumInMapper.updateForumIn(forumIn);
    }

    /**
     * 删除入账记录对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteForumInByIds(String ids)
    {
        return forumInMapper.deleteForumInByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除入账记录信息
     * 
     * @param id 入账记录ID
     * @return 结果
     */
    @Override
    public int deleteForumInById(String id)
    {
        return forumInMapper.deleteForumInById(id);
    }
}
