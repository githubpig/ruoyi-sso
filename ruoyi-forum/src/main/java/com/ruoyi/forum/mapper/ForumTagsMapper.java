package com.ruoyi.forum.mapper;

import java.util.List;
import com.ruoyi.forum.domain.ForumTags;

/**
 * 论坛标签Mapper接口
 * 
 * @author ruoyi
 * @date 2021-03-12
 */
public interface ForumTagsMapper 
{
    /**
     * 查询论坛标签
     * 
     * @param tagId 论坛标签ID
     * @return 论坛标签
     */
    public ForumTags selectForumTagsById(Long tagId);

    /**
     * 查询论坛标签列表
     * 
     * @param forumTags 论坛标签
     * @return 论坛标签集合
     */
    public List<ForumTags> selectForumTagsList(ForumTags forumTags);

    /**
     * 新增论坛标签
     * 
     * @param forumTags 论坛标签
     * @return 结果
     */
    public int insertForumTags(ForumTags forumTags);

    /**
     * 修改论坛标签
     * 
     * @param forumTags 论坛标签
     * @return 结果
     */
    public int updateForumTags(ForumTags forumTags);

    /**
     * 删除论坛标签
     * 
     * @param tagId 论坛标签ID
     * @return 结果
     */
    public int deleteForumTagsById(Long tagId);

    /**
     * 批量删除论坛标签
     * 
     * @param tagIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteForumTagsByIds(String[] tagIds);


    public List<ForumTags> selectForumTagsAll();

}
