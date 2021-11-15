package com.ruoyi.forum.service.impl;

import java.util.List;

import com.google.common.collect.Lists;
import com.ruoyi.cms.domain.Tags;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.forum.mapper.ForumTagsMapper;
import com.ruoyi.forum.domain.ForumTags;
import com.ruoyi.forum.service.IForumTagsService;
import com.ruoyi.common.core.text.Convert;

/**
 * 论坛标签Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-03-12
 */
@Service
public class ForumTagsServiceImpl implements IForumTagsService 
{
    @Autowired
    private ForumTagsMapper forumTagsMapper;

    /**
     * 查询论坛标签
     * 
     * @param tagId 论坛标签ID
     * @return 论坛标签
     */
    @Override
    public ForumTags selectForumTagsById(Long tagId)
    {
        return forumTagsMapper.selectForumTagsById(tagId);
    }

    /**
     * 查询论坛标签列表
     * 
     * @param forumTags 论坛标签
     * @return 论坛标签
     */
    @Override
    public List<ForumTags> selectForumTagsList(ForumTags forumTags)
    {
        return forumTagsMapper.selectForumTagsList(forumTags);
    }

    /**
     * 新增论坛标签
     * 
     * @param forumTags 论坛标签
     * @return 结果
     */
    @Override
    public int insertForumTags(ForumTags forumTags)
    {
        return forumTagsMapper.insertForumTags(forumTags);
    }

    /**
     * 修改论坛标签
     * 
     * @param forumTags 论坛标签
     * @return 结果
     */
    @Override
    public int updateForumTags(ForumTags forumTags)
    {
        return forumTagsMapper.updateForumTags(forumTags);
    }

    /**
     * 删除论坛标签对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteForumTagsByIds(String ids)
    {
        return forumTagsMapper.deleteForumTagsByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除论坛标签信息
     * 
     * @param tagId 论坛标签ID
     * @return 结果
     */
    @Override
    public int deleteForumTagsById(Long tagId)
    {
        return forumTagsMapper.deleteForumTagsById(tagId);
    }


    @Override
    public List<ForumTags> selectForumTagsAll() {
        return forumTagsMapper.selectForumTagsAll();
    }

    @Override
    public List<ForumTags> selectSelectedForumTagsAll(String selectedIds) {

        List<ForumTags> tags=this.selectForumTagsAll();

        if(StringUtils.isNotEmpty(selectedIds)){
            if(selectedIds.endsWith(",")){
                selectedIds=selectedIds.substring(0,selectedIds.length()-1);
            }
            String[] arr=Convert.toStrArray(selectedIds);
            List<String> list= Lists.newArrayList(arr);
            tags.forEach(t->{
                if(list.contains(t.getTagId().toString())){
                    t.setSelected(true);
                }
            });
        }
        return tags;
    }
}
