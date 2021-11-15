package com.ruoyi.forum.service.impl;

import java.util.List;

import com.ruoyi.common.utils.Guid;
import com.ruoyi.framework.util.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.forum.mapper.ForumOutMapper;
import com.ruoyi.forum.domain.ForumOut;
import com.ruoyi.forum.service.IForumOutService;
import com.ruoyi.common.core.text.Convert;

/**
 * 出账记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-03-19
 */
@Service
public class ForumOutServiceImpl implements IForumOutService 
{
    @Autowired
    private ForumOutMapper forumOutMapper;

    /**
     * 查询出账记录
     * 
     * @param id 出账记录ID
     * @return 出账记录
     */
    @Override
    public ForumOut selectForumOutById(String id)
    {
        return forumOutMapper.selectForumOutById(id);
    }

    /**
     * 查询出账记录列表
     * 
     * @param forumOut 出账记录
     * @return 出账记录
     */
    @Override
    public List<ForumOut> selectForumOutList(ForumOut forumOut)
    {
        forumOut.setUserId(ShiroUtils.getUserId().toString());
        return forumOutMapper.selectForumOutList(forumOut);
    }

    /**
     * 新增出账记录
     * 
     * @param forumOut 出账记录
     * @return 结果
     */
    @Override
    public int insertForumOut(ForumOut forumOut)
    {
        forumOut.setId(Guid.get());
        forumOut.setUserId(ShiroUtils.getUserId().toString());
        return forumOutMapper.insertForumOut(forumOut);
    }

    /**
     * 修改出账记录
     * 
     * @param forumOut 出账记录
     * @return 结果
     */
    @Override
    public int updateForumOut(ForumOut forumOut)
    {
        return forumOutMapper.updateForumOut(forumOut);
    }

    /**
     * 删除出账记录对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteForumOutByIds(String ids)
    {
        return forumOutMapper.deleteForumOutByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除出账记录信息
     * 
     * @param id 出账记录ID
     * @return 结果
     */
    @Override
    public int deleteForumOutById(String id)
    {
        return forumOutMapper.deleteForumOutById(id);
    }
}
