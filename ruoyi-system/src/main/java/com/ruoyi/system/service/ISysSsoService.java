package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysSso;

/**
 * sso集成应用Service接口
 * 
 * @author ruoyi
 * @date 2021-11-13
 */
public interface ISysSsoService 
{
    /**
     * 查询sso集成应用
     * 
     * @param id sso集成应用ID
     * @return sso集成应用
     */
    public SysSso selectSysSsoById(Integer id);

    /**
     * 查询sso集成应用列表
     * 
     * @param sysSso sso集成应用
     * @return sso集成应用集合
     */
    public List<SysSso> selectSysSsoList(SysSso sysSso);
    public List<String> selectSysSsoListByIds(String ids);
    /**
     * 新增sso集成应用
     * 
     * @param sysSso sso集成应用
     * @return 结果
     */
    public int insertSysSso(SysSso sysSso);

    /**
     * 修改sso集成应用
     * 
     * @param sysSso sso集成应用
     * @return 结果
     */
    public int updateSysSso(SysSso sysSso);

    /**
     * 批量删除sso集成应用
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysSsoByIds(String ids);

    /**
     * 删除sso集成应用信息
     * 
     * @param id sso集成应用ID
     * @return 结果
     */
    public int deleteSysSsoById(Integer id);
}
