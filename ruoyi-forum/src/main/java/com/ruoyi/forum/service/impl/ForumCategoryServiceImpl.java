package com.ruoyi.forum.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.forum.mapper.ForumCategoryMapper;
import com.ruoyi.forum.domain.ForumCategory;
import com.ruoyi.forum.service.IForumCategoryService;
import com.ruoyi.common.core.text.Convert;

/**
 * 论坛栏目Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-03-17
 */
@Service
public class ForumCategoryServiceImpl implements IForumCategoryService 
{
    @Autowired
    private ForumCategoryMapper forumCategoryMapper;

    /**
     * 查询论坛栏目
     * 
     * @param categoryId 论坛栏目ID
     * @return 论坛栏目
     */
    @Override
    public ForumCategory selectForumCategoryById(Long categoryId)
    {
        return forumCategoryMapper.selectForumCategoryById(categoryId);
    }

    /**
     * 查询论坛栏目列表
     * 
     * @param forumCategory 论坛栏目
     * @return 论坛栏目
     */
    @Override
    public List<ForumCategory> selectForumCategoryList(ForumCategory forumCategory)
    {
        return forumCategoryMapper.selectForumCategoryList(forumCategory);
    }

    /**
     * 新增论坛栏目
     * 
     * @param forumCategory 论坛栏目
     * @return 结果
     */
    @Override
    public int insertForumCategory(ForumCategory forumCategory)
    {
        forumCategory.setCreateTime(DateUtils.getNowDate());
        return forumCategoryMapper.insertForumCategory(forumCategory);
    }

    /**
     * 修改论坛栏目
     * 
     * @param forumCategory 论坛栏目
     * @return 结果
     */
    @Override
    public int updateForumCategory(ForumCategory forumCategory)
    {
        forumCategory.setUpdateTime(DateUtils.getNowDate());
        return forumCategoryMapper.updateForumCategory(forumCategory);
    }

    /**
     * 删除论坛栏目对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteForumCategoryByIds(String ids)
    {
        return forumCategoryMapper.deleteForumCategoryByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除论坛栏目信息
     * 
     * @param categoryId 论坛栏目ID
     * @return 结果
     */
    @Override
    public int deleteForumCategoryById(Long categoryId)
    {
        return forumCategoryMapper.deleteForumCategoryById(categoryId);
    }
}
