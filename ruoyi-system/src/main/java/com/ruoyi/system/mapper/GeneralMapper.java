package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GeneralMapper {

    void generalInsert(@Param("tableName") String tableName,@Param("data") Map<String, Object> data);
    List<String> selectPrimaryKeys(@Param("tableName") String tableName);
    List<String> selectAllColumns(@Param("tableName") String tableName);
    List<String> selectAllTables();
    List<String> selectColumnExtraInfo(@Param("tableName") String tableName,@Param("columnName") String columnName);
    List<Map> selectByMap(@Param("tableName") String tableName,@Param("data") Map<String, Object> data);
}
