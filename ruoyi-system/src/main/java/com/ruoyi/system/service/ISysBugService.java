package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysBug;

/**
 * bugService接口
 * 
 * @author ruoyi
 * @date 2021-07-15
 */
public interface ISysBugService 
{
    /**
     * 查询bug
     * 
     * @param id bugID
     * @return bug
     */
    public SysBug selectSysBugById(Long id);

    /**
     * 查询bug列表
     * 
     * @param sysBug bug
     * @return bug集合
     */
    public List<SysBug> selectSysBugList(SysBug sysBug);

    /**
     * 新增bug
     * 
     * @param sysBug bug
     * @return 结果
     */
    public int insertSysBug(SysBug sysBug);

    /**
     * 修改bug
     * 
     * @param sysBug bug
     * @return 结果
     */
    public int updateSysBug(SysBug sysBug);

    /**
     * 批量删除bug
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysBugByIds(String ids);

    /**
     * 删除bug信息
     * 
     * @param id bugID
     * @return 结果
     */
    public int deleteSysBugById(Long id);
}
