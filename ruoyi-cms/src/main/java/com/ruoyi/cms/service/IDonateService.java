package com.ruoyi.cms.service;

import java.util.List;
import com.ruoyi.cms.domain.Donate;

/**
 * 捐赠记录Service接口
 * 
 * @author ruoyi
 * @date 2021-07-16
 */
public interface IDonateService 
{
    /**
     * 查询捐赠记录
     * 
     * @param id 捐赠记录ID
     * @return 捐赠记录
     */
    public Donate selectDonateById(Long id);

    /**
     * 查询捐赠记录列表
     * 
     * @param donate 捐赠记录
     * @return 捐赠记录集合
     */
    public List<Donate> selectDonateList(Donate donate);

    /**
     * 新增捐赠记录
     * 
     * @param donate 捐赠记录
     * @return 结果
     */
    public int insertDonate(Donate donate);

    /**
     * 修改捐赠记录
     * 
     * @param donate 捐赠记录
     * @return 结果
     */
    public int updateDonate(Donate donate);

    /**
     * 批量删除捐赠记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteDonateByIds(String ids);

    /**
     * 删除捐赠记录信息
     * 
     * @param id 捐赠记录ID
     * @return 结果
     */
    public int deleteDonateById(Long id);
}
