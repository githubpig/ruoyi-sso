package com.ruoyi.forum.service.impl;

import java.util.List;
import java.util.Map;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.forum.mapper.ForumMessageMapper;
import com.ruoyi.forum.domain.ForumMessage;
import com.ruoyi.forum.service.IForumMessageService;
import com.ruoyi.common.core.text.Convert;

/**
 * 消息Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-03-24
 */
@Service
public class ForumMessageServiceImpl implements IForumMessageService 
{
    @Autowired
    private ForumMessageMapper forumMessageMapper;

    /**
     * 查询消息
     * 
     * @param id 消息ID
     * @return 消息
     */
    @Override
    public ForumMessage selectForumMessageById(Long id)
    {
        return forumMessageMapper.selectForumMessageById(id);
    }

    /**
     * 查询消息列表
     * 
     * @param forumMessage 消息
     * @return 消息
     */
    @Override
    public List<ForumMessage> selectForumMessageList(ForumMessage forumMessage)
    {
        return forumMessageMapper.selectForumMessageList(forumMessage);
    }

    /**
     * 新增消息
     * 
     * @param forumMessage 消息
     * @return 结果
     */
    @Override
    public int insertForumMessage(ForumMessage forumMessage)
    {
        SysUser user= ShiroUtils.getSysUser();
        forumMessage.setFromId(user.getUserId().toString());
        forumMessage.setFromName(user.getUserName());
        forumMessage.setCreateTime(DateUtils.getNowDate());
        return forumMessageMapper.insertForumMessage(forumMessage);
    }

    /**
     * 修改消息
     * 
     * @param forumMessage 消息
     * @return 结果
     */
    @Override
    public int updateForumMessage(ForumMessage forumMessage)
    {
        return forumMessageMapper.updateForumMessage(forumMessage);
    }

    /**
     * 删除消息对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteForumMessageByIds(String ids)
    {
        return forumMessageMapper.deleteForumMessageByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除消息信息
     * 
     * @param id 消息ID
     * @return 结果
     */
    @Override
    public int deleteForumMessageById(Long id)
    {
        return forumMessageMapper.deleteForumMessageById(id);
    }

    @Override
    public List<ForumMessage> selectByMsgType(String type, String userId) {
        return forumMessageMapper.selectByMsgType(type,userId);
    }

    @Override
    public List<ForumMessage> selectByAllType(String userId) {
        return forumMessageMapper.selectByAllType(userId);
    }

    @Override
    public int updateReadFlag(String ids) {
        return forumMessageMapper.updateReadFlag(Convert.toStrArray(ids));
    }

    @Override
    public Map countMessages(String userId) {
        return forumMessageMapper.countMessages(userId);
    }

    @Override
    public List<ForumMessage> selectComments(String userId) {
        return forumMessageMapper.selectComments(userId);
    }

    @Override
    public List<ForumMessage> selectPrivateMessage(String userId) {
        return forumMessageMapper.selectPrivateMessage(userId);
    }
}
