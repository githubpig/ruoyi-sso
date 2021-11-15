package com.ruoyi.forum.service.impl;

import java.util.List;

import com.ruoyi.framework.util.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.forum.mapper.ForumReportMapper;
import com.ruoyi.forum.domain.ForumReport;
import com.ruoyi.forum.service.IForumReportService;
import com.ruoyi.common.core.text.Convert;

/**
 * 举报信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-03-15
 */
@Service
public class ForumReportServiceImpl implements IForumReportService 
{
    @Autowired
    private ForumReportMapper forumReportMapper;

    /**
     * 查询举报信息
     * 
     * @param id 举报信息ID
     * @return 举报信息
     */
    @Override
    public ForumReport selectForumReportById(Long id)
    {
        return forumReportMapper.selectForumReportById(id);
    }

    /**
     * 查询举报信息列表
     * 
     * @param forumReport 举报信息
     * @return 举报信息
     */
    @Override
    public List<ForumReport> selectForumReportList(ForumReport forumReport)
    {
        return forumReportMapper.selectForumReportList(forumReport);
    }

    /**
     * 新增举报信息
     * 
     * @param forumReport 举报信息
     * @return 结果
     */
    @Override
    public int insertForumReport(ForumReport forumReport)
    {
        String userId= ShiroUtils.getUserId().toString();
        forumReport.setReportYhid(userId);
        return forumReportMapper.insertForumReport(forumReport);
    }

    /**
     * 修改举报信息
     * 
     * @param forumReport 举报信息
     * @return 结果
     */
    @Override
    public int updateForumReport(ForumReport forumReport)
    {
        return forumReportMapper.updateForumReport(forumReport);
    }

    /**
     * 删除举报信息对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteForumReportByIds(String ids)
    {
        return forumReportMapper.deleteForumReportByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除举报信息信息
     * 
     * @param id 举报信息ID
     * @return 结果
     */
    @Override
    public int deleteForumReportById(Long id)
    {
        return forumReportMapper.deleteForumReportById(id);
    }

    @Override
    public int selectExist(String target_id, String report_yhid) {
        return forumReportMapper.selectExist(target_id,report_yhid);
    }

    @Override
    public int dealForumReport(ForumReport forumReport) {
        return forumReportMapper.dealForumReport(forumReport);
    }
}
