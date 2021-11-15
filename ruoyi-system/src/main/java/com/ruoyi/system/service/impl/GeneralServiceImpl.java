package com.ruoyi.system.service.impl;

import com.ruoyi.system.mapper.GeneralMapper;
import com.ruoyi.system.service.IGeneralService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class GeneralServiceImpl implements IGeneralService {
    @Autowired
    private GeneralMapper generalMapper;

    @Override
    public void generalInsert(String tableName, Map<String, Object> data) {
        generalMapper.generalInsert(tableName,data);
    }

    @Override
    public List<String> selectPrimaryKeys(String tableName) {
        return generalMapper.selectPrimaryKeys(tableName);
    }

    @Override
    public List<String> selectAllColumns(String tableName) {
        return generalMapper.selectAllColumns(tableName);
    }

    @Override
    public List<String> selectAllTables() {
        return generalMapper.selectAllTables();
    }

    @Override
    public boolean isAutoColumn(String tableName, String columnName) {
        List<String> list=generalMapper.selectColumnExtraInfo(tableName,columnName);
        if(CollectionUtils.isNotEmpty(list)){
            String extra=list.get(0);
            if("auto_increment".equals(extra)){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Map> selectByMap(String tableName, Map<String, Object> data) {
        return generalMapper.selectByMap(tableName,data);
    }
}
