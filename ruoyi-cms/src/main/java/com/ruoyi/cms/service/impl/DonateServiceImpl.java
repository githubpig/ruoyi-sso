package com.ruoyi.cms.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.cms.mapper.DonateMapper;
import com.ruoyi.cms.domain.Donate;
import com.ruoyi.cms.service.IDonateService;
import com.ruoyi.common.core.text.Convert;

/**
 * 捐赠记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-07-16
 */
@Service
public class DonateServiceImpl implements IDonateService 
{
    @Autowired
    private DonateMapper donateMapper;

    /**
     * 查询捐赠记录
     * 
     * @param id 捐赠记录ID
     * @return 捐赠记录
     */
    @Override
    public Donate selectDonateById(Long id)
    {
        return donateMapper.selectDonateById(id);
    }

    /**
     * 查询捐赠记录列表
     * 
     * @param donate 捐赠记录
     * @return 捐赠记录
     */
    @Override
    public List<Donate> selectDonateList(Donate donate)
    {
        return donateMapper.selectDonateList(donate);
    }

    /**
     * 新增捐赠记录
     * 
     * @param donate 捐赠记录
     * @return 结果
     */
    @Override
    public int insertDonate(Donate donate)
    {
        donate.setCreateTime(DateUtils.getNowDate());
        return donateMapper.insertDonate(donate);
    }

    /**
     * 修改捐赠记录
     * 
     * @param donate 捐赠记录
     * @return 结果
     */
    @Override
    public int updateDonate(Donate donate)
    {
        return donateMapper.updateDonate(donate);
    }

    /**
     * 删除捐赠记录对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteDonateByIds(String ids)
    {
        return donateMapper.deleteDonateByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除捐赠记录信息
     * 
     * @param id 捐赠记录ID
     * @return 结果
     */
    @Override
    public int deleteDonateById(Long id)
    {
        return donateMapper.deleteDonateById(id);
    }
}
