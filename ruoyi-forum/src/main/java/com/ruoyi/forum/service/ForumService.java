package com.ruoyi.forum.service;


import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import com.ruoyi.common.config.Global;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.forum.domain.ForumCategory;
import com.ruoyi.forum.domain.ForumQuestion;
import com.ruoyi.forum.domain.ForumTags;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service("forum")
public class ForumService {


    @Autowired
    private IForumCategoryService forumCategoryService;
    @Autowired
    private IForumQuestionService forumQuestionService;

    @Autowired
    private ISysUserService userService;
    @Autowired
    private ForumUserService forumUserService;
    @Autowired
    private IForumTagsService forumTagsService;
    @Autowired
    private  IForumCommentService forumCommentService;

    private Cache<String,Object> forumCache= CacheUtil.newTimedCache(1000*60*10);//10分钟有效时间，过期后重新数据库取值
    private static final String KEY_CATEGORY="category";
    private static final String KEY_TOP="top";
    private static final String KEY_NEW_20="new20";
    public void clearCache(){
        forumCache.clear();
    }
    /**
     * 查询导航栏目
     * @return
     */
    public Object selectNavCategoriesAll(){

        List<ForumCategory> list=null;
        if(Boolean.valueOf(Global.isCacheEnabled())){
            list=( List<ForumCategory>)forumCache.get(KEY_CATEGORY,false);
        }
        if(list==null){
            ForumCategory queryForm=new ForumCategory();
            queryForm.setStatus(0);
            list =forumCategoryService.selectForumCategoryList(queryForm);
            if(Boolean.valueOf(Global.isCacheEnabled())){
                forumCache.put(KEY_CATEGORY,list);
            }
        }
        return list;
    }
    private static final int limit=7;
    public Object selectNavCategories(){
        List<ForumCategory> list=( List<ForumCategory>)this.selectNavCategoriesAll();

        if(CollectionUtils.isNotEmpty(list)){
            if(list.size()>limit){
                list=list.subList(0,limit);
            }
        }
        return list;
    }
    public Object selectNavCategoriesMore(){
        List<ForumCategory> list=( List<ForumCategory>)this.selectNavCategoriesAll();
        int total=list.size();

        if(CollectionUtils.isNotEmpty(list)){
            if(list.size()>limit){
                list=list.subList(limit,total);
            }
        }
        return list;
    }
    public boolean showMore(){
        List<ForumCategory> list=( List<ForumCategory>)this.selectNavCategoriesAll();
        if(list.size()>limit){
            return true;
        }
        return false;
    }

    /**
     * 查询置顶帖子
     * @return
     */
    public Object selectTopQuestion(){
        List<ForumQuestion> list=null;

        if(Boolean.valueOf(Global.isCacheEnabled())){
            list=( List<ForumQuestion>)forumCache.get(KEY_TOP,false);
        }

        if(list==null){
            ForumQuestion queryForm=new ForumQuestion();
            queryForm.setQuestionRegion(KEY_TOP);
            queryForm.setFront(1);
            list =forumQuestionService.selectForumQuestionList(queryForm);

            if(Boolean.valueOf(Global.isCacheEnabled())){
                forumCache.put(KEY_TOP,list);
            }
        }



        list.forEach(a->{
            //评论数
            int m=forumCommentService.selectCommentCount(a.getId().toString());
            a.setCommentNum(m);
        });
        list.forEach(a->{
            //作者名称
            //a.getYhid()
            SysUser user= (SysUser) forumCache.get("user_"+a.getYhid().toString());
            if(user==null){
                user= userService.selectUserById(Long.valueOf(a.getYhid()));
                forumCache.put("user_"+a.getYhid(),user);
            }
            a.setAuthor(user.getUserName());
            a.setAvatar(getAvatarPath(user));
        });

        return list;
    }

    /**
     * 查询最新的20个帖子
     * @return
     */
    public Object selectNew20Question(){
        List<ForumQuestion> list=null;
        if(Boolean.valueOf(Global.isCacheEnabled())){
            list=( List<ForumQuestion>)forumCache.get(KEY_NEW_20,false);
        }
        if(list==null){
            list =forumQuestionService.selectNew20Question();
            if(Boolean.valueOf(Global.isCacheEnabled())){
                forumCache.put(KEY_NEW_20,list);
            }
        }

        list.forEach(a->{
            String cid = a.getCategoryId();
            ForumCategory category=(ForumCategory)forumCache.get("category_"+cid);
            if(category==null){
                category=forumCategoryService.selectForumCategoryById(Long.valueOf(cid));
                if(category!=null){
                    forumCache.put("category_"+cid,category);
                }
            }
            if(category!=null){
                a.setForumCategory(category);
                a.setForumCategoryName(category.getCategoryName());
            }
        });

        list.forEach(a->{
            //评论数
            int m=forumCommentService.selectCommentCount(a.getId().toString());
            a.setCommentNum(m);
        });
        list.forEach(a->{
            //作者名称
            //a.getYhid()
            SysUser user= (SysUser) forumCache.get("user_"+a.getYhid().toString());
            if(user==null){
                user= userService.selectUserById(Long.valueOf(a.getYhid()));
                forumCache.put("user_"+a.getYhid(),user);
            }
            a.setAuthor(user.getUserName());
            a.setAvatar(getAvatarPath(user));
        });

        return list;
    }
    /**
     * 查询活跃用户前6个人
     * @return
     */
    public List<SysUser> selectActivityUsers(){
      List<SysUser> users= forumUserService.selectActivityUsers();
      users.forEach(a->{
          int n=forumQuestionService.selectQuestionCount(a.getUserId().toString());
          a.setQuestionCount(n);
      });
      return users;
    }
    public String getAvatarPath(SysUser user){
        if(StringUtils.isEmpty(user.getAvatar())){
            if("1".equals(user.getSex())){
                return "/forum/images/boy.png";
            }else{
                return "/forum/images/girl.png";
            }
        }
        return user.getAvatar();
    }
    private static final String KEY_TAGS="tags";
    /**
     * 获得标签云
     * @return
     */
    public Object getForumTagsCloud(){
        List<ForumTags> list =null;
        if(Boolean.valueOf(Global.isCacheEnabled())){
            list = ( List<ForumTags>)forumCache.get(KEY_TAGS,false);
        }
        if(list==null){
            list = forumTagsService.selectForumTagsAll();
            for(ForumTags tag:list){
                int n=forumQuestionService.questionTagCount(tag.getTagId().toString());
                if(n>0){
                    tag.setCount(n);
                }else {
                    tag.setCount(0);
                }
            }

            if(Boolean.valueOf(Global.isCacheEnabled())){
                forumCache.put(KEY_TAGS,list);
            }
        }
        return list;
    }
}
