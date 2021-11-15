package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysSsoMapper;
import com.ruoyi.system.domain.SysSso;
import com.ruoyi.system.service.ISysSsoService;
import com.ruoyi.common.core.text.Convert;

/**
 * sso集成应用Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-11-13
 */
@Service
public class SysSsoServiceImpl implements ISysSsoService 
{
    @Autowired
    private SysSsoMapper sysSsoMapper;

    /**
     * 查询sso集成应用
     * 
     * @param id sso集成应用ID
     * @return sso集成应用
     */
    @Override
    public SysSso selectSysSsoById(Integer id)
    {
        return sysSsoMapper.selectSysSsoById(id);
    }

    /**
     * 查询sso集成应用列表
     * 
     * @param sysSso sso集成应用
     * @return sso集成应用
     */
    @Override
    public List<SysSso> selectSysSsoList(SysSso sysSso)
    {
        return sysSsoMapper.selectSysSsoList(sysSso);
    }

    @Override
    public List<String> selectSysSsoListByIds(String ids) {
        return sysSsoMapper.selectSysSsoListByIds(Convert.toIntArray(ids));
    }

    /**
     * 新增sso集成应用
     * 
     * @param sysSso sso集成应用
     * @return 结果
     */
    @Override
    public int insertSysSso(SysSso sysSso)
    {
        return sysSsoMapper.insertSysSso(sysSso);
    }

    /**
     * 修改sso集成应用
     * 
     * @param sysSso sso集成应用
     * @return 结果
     */
    @Override
    public int updateSysSso(SysSso sysSso)
    {
        return sysSsoMapper.updateSysSso(sysSso);
    }

    /**
     * 删除sso集成应用对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteSysSsoByIds(String ids)
    {
        return sysSsoMapper.deleteSysSsoByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除sso集成应用信息
     * 
     * @param id sso集成应用ID
     * @return 结果
     */
    @Override
    public int deleteSysSsoById(Integer id)
    {
        return sysSsoMapper.deleteSysSsoById(id);
    }
}
