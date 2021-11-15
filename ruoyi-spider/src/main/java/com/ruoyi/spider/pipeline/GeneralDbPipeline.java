package com.ruoyi.spider.pipeline;

import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.service.IGeneralService;
import org.apache.commons.collections.CollectionUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeneralDbPipeline implements Pipeline {

    private String tableName;
    private IGeneralService generalService;
    private List<String> primaryKeys;
    private Boolean hasCreateTime;
    private String createTimeKey="";

    /**
     * 只能通过构造方法new出对象
     * @param tableName
     * @param generalService
     */
    public GeneralDbPipeline(String tableName,IGeneralService generalService){
        List<String> allTables=generalService.selectAllTables();
        List<String> temp=allTables.stream().filter(a->{return a.equals(tableName);}).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(temp)){
            throw new BusinessException("数据库中不存在表["+tableName+"]!");
        }
        this.tableName=tableName;
        this.generalService=generalService;
        List<String> keys = generalService.selectPrimaryKeys(this.tableName);
        this.primaryKeys=keys;
        List<String> allColumns=generalService.selectAllColumns(tableName);
        if(allColumns!=null&&allColumns.contains("create_time")){
            hasCreateTime=true;
            createTimeKey="create_time";
        }
        if(allColumns!=null&&allColumns.contains("createTime")){
            hasCreateTime=true;
            createTimeKey="createTime";
        }
    }
    @Override
    public void process(ResultItems resultItems, Task task) {
        Map<String,Object> data = resultItems.getAll();
        if(hasCreateTime){
            resultItems.getAll().put(createTimeKey, DateUtils.getTime());
        }
        for(String column:primaryKeys){
            if(data.containsKey(column)){
                boolean auto=generalService.isAutoColumn(tableName,column);
                if(auto){
                    data.remove(column);//自动增长的主键从数据中移除
                }
            }
        }
        generalService.generalInsert(tableName,data);
    }
}
