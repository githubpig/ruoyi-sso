package com.ruoyi.forum.service;

import java.util.List;
import com.ruoyi.forum.domain.ForumCategory;

/**
 * 论坛栏目Service接口
 * 
 * @author ruoyi
 * @date 2021-03-17
 */
public interface IForumCategoryService 
{
    /**
     * 查询论坛栏目
     * 
     * @param categoryId 论坛栏目ID
     * @return 论坛栏目
     */
    public ForumCategory selectForumCategoryById(Long categoryId);

    /**
     * 查询论坛栏目列表
     * 
     * @param forumCategory 论坛栏目
     * @return 论坛栏目集合
     */
    public List<ForumCategory> selectForumCategoryList(ForumCategory forumCategory);

    /**
     * 新增论坛栏目
     * 
     * @param forumCategory 论坛栏目
     * @return 结果
     */
    public int insertForumCategory(ForumCategory forumCategory);

    /**
     * 修改论坛栏目
     * 
     * @param forumCategory 论坛栏目
     * @return 结果
     */
    public int updateForumCategory(ForumCategory forumCategory);

    /**
     * 批量删除论坛栏目
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteForumCategoryByIds(String ids);

    /**
     * 删除论坛栏目信息
     * 
     * @param categoryId 论坛栏目ID
     * @return 结果
     */
    public int deleteForumCategoryById(Long categoryId);
}
