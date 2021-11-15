package com.ruoyi.system.service;
import java.util.List;
import java.util.Map;

public interface IGeneralService {

   public void generalInsert(String tableName,Map<String, Object> data);
   List<String> selectPrimaryKeys(String tableName);
   List<String> selectAllColumns(String tableName);
   List<String> selectAllTables();
   boolean isAutoColumn(String tableName,String columnName);
   List<Map> selectByMap(String tableName,Map<String, Object> data);
}
