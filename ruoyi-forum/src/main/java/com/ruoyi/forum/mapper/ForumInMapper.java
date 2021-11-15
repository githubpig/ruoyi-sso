package com.ruoyi.forum.mapper;

import java.util.List;
import com.ruoyi.forum.domain.ForumIn;

/**
 * 入账记录Mapper接口
 * 
 * @author ruoyi
 * @date 2021-03-19
 */
public interface ForumInMapper 
{
    /**
     * 查询入账记录
     * 
     * @param id 入账记录ID
     * @return 入账记录
     */
    public ForumIn selectForumInById(String id);

    /**
     * 查询入账记录列表
     * 
     * @param forumIn 入账记录
     * @return 入账记录集合
     */
    public List<ForumIn> selectForumInList(ForumIn forumIn);

    /**
     * 新增入账记录
     * 
     * @param forumIn 入账记录
     * @return 结果
     */
    public int insertForumIn(ForumIn forumIn);

    /**
     * 修改入账记录
     * 
     * @param forumIn 入账记录
     * @return 结果
     */
    public int updateForumIn(ForumIn forumIn);

    /**
     * 删除入账记录
     * 
     * @param id 入账记录ID
     * @return 结果
     */
    public int deleteForumInById(String id);

    /**
     * 批量删除入账记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteForumInByIds(String[] ids);
}
