package com.ruoyi.forum.domain;

import com.ruoyi.cms.domain.Category;
import com.ruoyi.cms.domain.Tags;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.List;

/**
 * 问题对象 forum_question
 * 
 * @author ruoyi
 * @date 2021-03-12
 */
public class ForumQuestion extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键，文章ID */
    private Integer id;

    /** 作者 */
    private String yhid;

    /** 专区 */
    @Excel(name = "专区")
    private String questionRegion;

    /** 作者 */
    private String author;

    /** 发布时间 */
    private String publishTime;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 频道栏目ID */
    @Excel(name = "频道栏目ID")
    private String categoryId;

    /** 标签 */
    @Excel(name = "标签")
    private String tags;

    /** 点击数 */
    private Long hit;

    /** 是否开启评论 */
    @Excel(name = "是否开启评论")
    private String commentFlag;

    /** 点赞数 */
    private Integer upVote;

    /** 收藏数 */
    private Long favourite;

    /** 状态标志 */
    @Excel(name = "状态标志")
    private Integer available;

    /** 删除标志 */
    @Excel(name = "删除标志")
    private Integer deleted;

    private int front;//扩展字段，用来区分是前台查询还是后台查询
    /** 文章内容 */
    private String content;

    /** 文章markdown源码 */
    private String content_markdown_source;

    private String tags_name;//扩展字段。标签名称
    private String forumCategoryName;
    private ForumCategory forumCategory;//栏目分类

    private List<ForumTags> tagList;//扩展字段
    private String favouriteTime;

    private int commentNum;//扩展字段，问题评论数

    private Integer favouriteFlag;//收藏标志


    private String attachment;//附件
    private int downloadType;//
    private int payType;//
    private int payCount;//
    private int hideType;//
    public String getAttachment() {
        return attachment;
    }

    public int getHideType() {
        return hideType;
    }

    public void setHideType(int hideType) {
        this.hideType = hideType;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public int getDownloadType() {
        return downloadType;
    }

    public void setDownloadType(int downloadType) {
        this.downloadType = downloadType;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public int getPayCount() {
        return payCount;
    }

    public void setPayCount(int payCount) {
        this.payCount = payCount;
    }

    public int getFront() {
        return front;
    }

    public void setFront(int front) {
        this.front = front;
    }

    private String avatar;//头像路径

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFavouriteTime() {
        return favouriteTime;
    }

    public void setFavouriteTime(String favouriteTime) {
        this.favouriteTime = favouriteTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent_markdown_source() {
        return content_markdown_source;
    }

    public void setContent_markdown_source(String content_markdown_source) {
        this.content_markdown_source = content_markdown_source;
    }

    public String getForumCategoryName() {
        return forumCategoryName;
    }

    public void setForumCategoryName(String forumCategoryName) {
        this.forumCategoryName = forumCategoryName;
    }

    public String getTags_name() {
        return tags_name;
    }

    public void setTags_name(String tags_name) {
        this.tags_name = tags_name;
    }


    public ForumCategory getForumCategory() {
        return forumCategory;
    }

    public void setForumCategory(ForumCategory forumCategory) {
        this.forumCategory = forumCategory;
    }

    public List<ForumTags> getTagList() {
        return tagList;
    }

    public void setTagList(List<ForumTags> tagList) {
        this.tagList = tagList;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }
    public void setYhid(String yhid) 
    {
        this.yhid = yhid;
    }

    public String getYhid() 
    {
        return yhid;
    }
    public void setQuestionRegion(String questionRegion) 
    {
        this.questionRegion = questionRegion;
    }

    public String getQuestionRegion() 
    {
        return questionRegion;
    }
    public void setAuthor(String author) 
    {
        this.author = author;
    }

    public String getAuthor() 
    {
        return author;
    }
    public void setPublishTime(String publishTime) 
    {
        this.publishTime = publishTime;
    }

    public String getPublishTime() 
    {
        return publishTime;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    public void setCategoryId(String categoryId) 
    {
        this.categoryId = categoryId;
    }

    public String getCategoryId() 
    {
        return categoryId;
    }
    public void setTags(String tags) 
    {
        this.tags = tags;
    }

    public String getTags() 
    {
        return tags;
    }
    public void setHit(Long hit) 
    {
        this.hit = hit;
    }

    public Long getHit() 
    {
        return hit;
    }
    public void setCommentFlag(String commentFlag)
    {
        this.commentFlag = commentFlag;
    }

    public String getCommentFlag()
    {
        return commentFlag;
    }

    public void setFavourite(Long favourite)
    {
        this.favourite = favourite;
    }

    public Long getFavourite() 
    {
        return favourite;
    }
    public void setAvailable(Integer available) 
    {
        this.available = available;
    }

    public Integer getAvailable() 
    {
        return available;
    }
    public void setDeleted(Integer deleted) 
    {
        this.deleted = deleted;
    }

    public Integer getDeleted() 
    {
        return deleted;
    }

    public Integer getUpVote() {
        return upVote;
    }

    public void setUpVote(Integer upVote) {
        this.upVote = upVote;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public Integer getFavouriteFlag() {
        return favouriteFlag;
    }

    public void setFavouriteFlag(int favouriteFlag) {
        this.favouriteFlag = favouriteFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("yhid", getYhid())
            .append("questionRegion", getQuestionRegion())
            .append("author", getAuthor())
            .append("publishTime", getPublishTime())
            .append("title", getTitle())
            .append("categoryId", getCategoryId())
            .append("tags", getTags())
            .append("hit", getHit())
            .append("commentFlag", getCommentFlag())
            .append("upVote", getUpVote())
            .append("favourite", getFavourite())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("available", getAvailable())
            .append("deleted", getDeleted())
            .toString();
    }
}
