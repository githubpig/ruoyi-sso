package com.ruoyi.forum.mapper;

import java.util.List;
import com.ruoyi.forum.domain.ForumReport;
import org.apache.ibatis.annotations.Param;

/**
 * 举报信息Mapper接口
 * 
 * @author ruoyi
 * @date 2021-03-15
 */
public interface ForumReportMapper 
{
    /**
     * 查询举报信息
     * 
     * @param id 举报信息ID
     * @return 举报信息
     */
    public ForumReport selectForumReportById(Long id);

    /**
     * 查询举报信息列表
     * 
     * @param forumReport 举报信息
     * @return 举报信息集合
     */
    public List<ForumReport> selectForumReportList(ForumReport forumReport);

    /**
     * 新增举报信息
     * 
     * @param forumReport 举报信息
     * @return 结果
     */
    public int insertForumReport(ForumReport forumReport);

    /**
     * 修改举报信息
     * 
     * @param forumReport 举报信息
     * @return 结果
     */
    public int updateForumReport(ForumReport forumReport);

    /**
     * 删除举报信息
     * 
     * @param id 举报信息ID
     * @return 结果
     */
    public int deleteForumReportById(Long id);

    /**
     * 批量删除举报信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteForumReportByIds(String[] ids);

    /**
     * 查询是否已经举报
     * @param target_id
     * @param report_yhid
     * @return
     */
    public int selectExist(@Param("target_id") String target_id, @Param("report_yhid") String report_yhid);

    /**
     * 处理举报信息
     *
     * @param forumReport 举报信息
     * @return 结果
     */
    public int dealForumReport(ForumReport forumReport);

}
