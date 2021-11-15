package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysBugMapper;
import com.ruoyi.system.domain.SysBug;
import com.ruoyi.system.service.ISysBugService;
import com.ruoyi.common.core.text.Convert;

/**
 * bugService业务层处理
 *
 * @author ruoyi
 * @date 2021-07-15
 */
@Service
public class SysBugServiceImpl implements ISysBugService
{
    @Autowired
    private SysBugMapper sysBugMapper;

    /**
     * 查询bug
     *
     * @param id bugID
     * @return bug
     */
    @Override
    public SysBug selectSysBugById(Long id)
    {
        return sysBugMapper.selectSysBugById(id);
    }

    /**
     * 查询bug列表
     *
     * @param sysBug bug
     * @return bug
     */
    @Override
    public List<SysBug> selectSysBugList(SysBug sysBug)
    {
        return sysBugMapper.selectSysBugList(sysBug);
    }

    /**
     * 新增bug
     *
     * @param sysBug bug
     * @return 结果
     */
    @Override
    public int insertSysBug(SysBug sysBug)
    {
        sysBug.setCreateTime(DateUtils.getNowDate());
        return sysBugMapper.insertSysBug(sysBug);
    }

    /**
     * 修改bug
     *
     * @param sysBug bug
     * @return 结果
     */
    @Override
    public int updateSysBug(SysBug sysBug)
    {
        return sysBugMapper.updateSysBug(sysBug);
    }

    /**
     * 删除bug对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteSysBugByIds(String ids)
    {
        return sysBugMapper.deleteSysBugByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除bug信息
     *
     * @param id bugID
     * @return 结果
     */
    @Override
    public int deleteSysBugById(Long id)
    {
        return sysBugMapper.deleteSysBugById(id);
    }
}
