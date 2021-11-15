package com.ruoyi.forum.service;

import java.util.List;
import com.ruoyi.forum.domain.ForumOut;

/**
 * 出账记录Service接口
 * 
 * @author ruoyi
 * @date 2021-03-19
 */
public interface IForumOutService 
{
    /**
     * 查询出账记录
     * 
     * @param id 出账记录ID
     * @return 出账记录
     */
    public ForumOut selectForumOutById(String id);

    /**
     * 查询出账记录列表
     * 
     * @param forumOut 出账记录
     * @return 出账记录集合
     */
    public List<ForumOut> selectForumOutList(ForumOut forumOut);

    /**
     * 新增出账记录
     * 
     * @param forumOut 出账记录
     * @return 结果
     */
    public int insertForumOut(ForumOut forumOut);

    /**
     * 修改出账记录
     * 
     * @param forumOut 出账记录
     * @return 结果
     */
    public int updateForumOut(ForumOut forumOut);

    /**
     * 批量删除出账记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteForumOutByIds(String ids);

    /**
     * 删除出账记录信息
     * 
     * @param id 出账记录ID
     * @return 结果
     */
    public int deleteForumOutById(String id);
}
