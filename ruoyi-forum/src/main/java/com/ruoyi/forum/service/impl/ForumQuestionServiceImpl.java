package com.ruoyi.forum.service.impl;

import java.util.List;
import java.util.Map;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.Guid;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.forum.domain.ForumCategory;
import com.ruoyi.forum.service.IForumCategoryService;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.forum.mapper.ForumQuestionMapper;
import com.ruoyi.forum.domain.ForumQuestion;
import com.ruoyi.forum.service.IForumQuestionService;
import com.ruoyi.common.core.text.Convert;

/**
 * 问题Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-03-12
 */
@Service
public class ForumQuestionServiceImpl implements IForumQuestionService 
{

    @Autowired
    private IForumCategoryService forumCategoryService;
    @Autowired
    private ForumQuestionMapper forumQuestionMapper;
    
    private Cache<String, ForumCategory> myCache= CacheUtil.newLFUCache(100);
    /**
     * 查询问题
     * 
     * @param id 问题ID
     * @return 问题
     */
    @Override
    public ForumQuestion selectForumQuestionById(String id)
    {
        ForumQuestion forumQuestion=forumQuestionMapper.selectForumQuestionById(id);
        Map<String, Object> m = forumQuestionMapper.getQuestionContent(id);
        if(m!=null){
            forumQuestion.setContent(String.valueOf(m.get("content")));
            forumQuestion.setContent_markdown_source(String.valueOf(m.get("content_markdown_source")));
        }
        selectCategory(forumQuestion);
        return forumQuestion;
    }

    /**
     * 查询问题列表
     * 
     * @param forumQuestion 问题
     * @return 问题
     */
    @Override
    public List<ForumQuestion> selectForumQuestionList(ForumQuestion forumQuestion)
    {
        List<ForumQuestion> forumQuestions=forumQuestionMapper.selectForumQuestionList(forumQuestion);
        selectCategory(forumQuestions);
        return forumQuestions;
    }

    @Override
    public List<ForumQuestion> selectForumQuestionList_Back(ForumQuestion forumQuestion) {
        SysUser user=ShiroUtils.getSysUser();
        if(!user.isAdmin()){
            //不是管理员只能查询自己的帖子
            forumQuestion.setYhid(user.getUserId().toString());
        }
        List<ForumQuestion> forumQuestions=forumQuestionMapper.selectForumQuestionList(forumQuestion);
        selectCategory(forumQuestions);
        return forumQuestions;
    }

    /**
     * 新增问题
     * 
     * @param forumQuestion 问题
     * @return 结果
     */
    @Override
    public int insertForumQuestion(ForumQuestion forumQuestion)
    {
        //forumQuestion.setId(Guid.get());
        forumQuestion.setCreateTime(DateUtils.getNowDate());
        forumQuestion.setUpdateTime(DateUtils.getNowDate());
        SysUser user= ShiroUtils.getSysUser();
        forumQuestion.setYhid(user.getUserId().toString());
        forumQuestion.setDeleted(0);
        String tags=forumQuestion.getTags();
        if(StringUtils.isNotEmpty(tags)){
            if(!tags.endsWith(",")){
                tags+=",";
                forumQuestion.setTags(tags);
            }
        }
        forumQuestion.setAuthor(user.getUserName());

        if(forumQuestion.getCommentFlag()==null){
            forumQuestion.setCommentFlag("0");
        }
        if("on".equals(forumQuestion.getCommentFlag())){
            forumQuestion.setCommentFlag("1");
        }
        if("off".equals(forumQuestion.getCommentFlag())){
            forumQuestion.setCommentFlag("0");
        }
        int n=forumQuestionMapper.insertForumQuestion(forumQuestion);
        n=forumQuestionMapper.insertQuestionContent(forumQuestion);
        return n;
    }

    /**
     * 修改问题
     * 
     * @param forumQuestion 问题
     * @return 结果
     */
    @Override
    public int updateForumQuestion(ForumQuestion forumQuestion)
    {
        forumQuestion.setUpdateTime(DateUtils.getNowDate());
        String tags=forumQuestion.getTags();
        if(StringUtils.isNotEmpty(tags)){
            if(!tags.endsWith(",")){
                tags+=",";
                forumQuestion.setTags(tags);
            }
        }
        if(forumQuestion.getCommentFlag()==null){
            forumQuestion.setCommentFlag("0");
        }
        if("on".equals(forumQuestion.getCommentFlag())){
            forumQuestion.setCommentFlag("1");
        }
        if("off".equals(forumQuestion.getCommentFlag())){
            forumQuestion.setCommentFlag("0");
        }
        int n=forumQuestionMapper.updateForumQuestion(forumQuestion);
        n=forumQuestionMapper.updateQuestionContent(forumQuestion);
        return n;
    }

    @Override
    public int updateAvailable(ForumQuestion forumQuestion) {
        return forumQuestionMapper.updateAvailable(forumQuestion);
    }

    /**
     * 删除问题对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteForumQuestionByIds(String ids)
    {
        forumQuestionMapper.deleteQuestionContentByIds(Convert.toStrArray(ids));
        return forumQuestionMapper.deleteForumQuestionByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除问题信息
     * 
     * @param id 问题ID
     * @return 结果
     */
    @Override
    public int deleteForumQuestionById(String id)
    {
        return forumQuestionMapper.deleteForumQuestionById(id);
    }

    @Override
    public List<ForumQuestion> selectMyFavouriteList(ForumQuestion forumQuestion) {
        return forumQuestionMapper.selectMyFavouriteList(forumQuestion);
    }

    @Override
    public int removeFavourite(String yhid, String question_id) {
        return forumQuestionMapper.removeFavourite(yhid,question_id);
    }

    @Override
    public int addFavourite(String yhid, String question_id) {
        return forumQuestionMapper.addFavourite(yhid,question_id);
    }

    @Override
    public Integer selectFavouriteFlag(String yhid, String question_id) {
        return forumQuestionMapper.selectFavouriteFlag(yhid,question_id);
    }

    @Override
    public int upVote(String id) {
        return forumQuestionMapper.upVote(id);
    }

    @Override
    public int questionLook(String id) {
        return forumQuestionMapper.questionLook(id);
    }

    @Override
    public int setTop(String id) {
        return forumQuestionMapper.setTop(id);
    }

    @Override
    public int setRecommend(String id) {
        return forumQuestionMapper.setRecommend(id);
    }

    @Override
    public int setGood(String id) {
        return forumQuestionMapper.setGood(id);
    }

    @Override
    public List<ForumQuestion> selectNew20Question() {
        return forumQuestionMapper.selectNew20Question();
    }

    @Override
    public int selectQuestionCount(String userId) {
        return forumQuestionMapper.selectQuestionCount(userId);
    }

    @Override
    public void batchPublish(String[] ids) {
        forumQuestionMapper.batchPublish(ids);
    }

    @Override
    public int questionTagCount(String tagId) {
        return forumQuestionMapper.questionTagCount(tagId);
    }


    private void selectCategory(List<ForumQuestion> list){
        list.forEach(a->{
            String cid = a.getCategoryId();
            ForumCategory category=myCache.get(cid);
            if(category==null){
                category=forumCategoryService.selectForumCategoryById(Long.valueOf(cid));
                if(category!=null){
                    myCache.put(cid,category);
                }
            }
            if(category!=null){
                a.setForumCategory(category);
                a.setForumCategoryName(category.getCategoryName());
            }
        });
    }
    private void selectCategory(ForumQuestion forumQuestion){
        if(forumQuestion==null){
            return;
        }
        String cid = forumQuestion.getCategoryId();
        ForumCategory category=myCache.get(cid);
        if(category==null){
            category=forumCategoryService.selectForumCategoryById(Long.valueOf(cid));
            if(category!=null){
                myCache.put(cid,category);
            }
        }
        if(category!=null){
            forumQuestion.setForumCategory(category);
        }
    }
}
