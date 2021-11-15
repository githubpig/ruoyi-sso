package com.ruoyi.forum.mapper;

import java.util.List;
import java.util.Map;

import com.ruoyi.forum.domain.ForumMessage;
import org.apache.ibatis.annotations.Param;

/**
 * 消息Mapper接口
 * 
 * @author ruoyi
 * @date 2021-03-24
 */
public interface ForumMessageMapper 
{
    /**
     * 查询消息
     * 
     * @param id 消息ID
     * @return 消息
     */
    public ForumMessage selectForumMessageById(Long id);

    /**
     * 查询消息列表
     * 
     * @param forumMessage 消息
     * @return 消息集合
     */
    public List<ForumMessage> selectForumMessageList(ForumMessage forumMessage);

    /**
     * 新增消息
     * 
     * @param forumMessage 消息
     * @return 结果
     */
    public int insertForumMessage(ForumMessage forumMessage);

    /**
     * 修改消息
     * 
     * @param forumMessage 消息
     * @return 结果
     */
    public int updateForumMessage(ForumMessage forumMessage);

    /**
     * 删除消息
     * 
     * @param id 消息ID
     * @return 结果
     */
    public int deleteForumMessageById(Long id);

    /**
     * 批量删除消息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteForumMessageByIds(String[] ids);


    public List<ForumMessage> selectByMsgType(@Param("type") String type, @Param("userId") String userId);

    public List<ForumMessage> selectByAllType(String userId);

    public int updateReadFlag(String[] ids);

    public Map countMessages(String userId);

    public List<ForumMessage> selectComments(String userId);

    public List<ForumMessage> selectPrivateMessage(String userId);
}
