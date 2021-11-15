package com.ruoyi.spider.config;

public class SpiderConstants {

    public static final String CONFIG_FILE_NAME = "/FastConfig.json";

    /*爬虫任务-待执行*/
    public static final String SPIDER_MISSION_STATUS_WAIT="wait";

    /*爬虫任务-待执行*/
    public static final String SPIDER_MISSION_STATUS_RUNNING="running";

    /*爬虫任务-待执行*/
    public static final String SPIDER_MISSION_STATUS_DONE="done";

    /*爬虫任务-待执行*/
    public static final String SPIDER_MISSION_STATUS_ERROR="error";
    /*字段提取类型-css规则*/
    public static final String FIELD_EXTRACT_TYPE_CSS="css";
    /*字段提取类型-xpath规则*/
    public static final String FIELD_EXTRACT_TYPE_XPATH="xpath";
    /*字段提取类型-xpath规则*/
    public static final String FIELD_EXTRACT_TYPE_CONSTANT="constant";
    /*URL 博客园首页*/
    public static final String URL_CNBLOGS="https://www.cnblogs.com";
    /*博客园目标URL正则*/
    public static final String TARGET_URL_CNBLOGS="https://www.cnblogs.com/\\w+/p/\\d+.html";

    /*字段值处理类型-替换*/
    public static final String FIELD_PROCESS_TYPE_REPLACE="replace";
    /*字段值处理类型-替换掉html标签获得纯文本*/
    public static final String FIELD_PROCESS_TYPE_REPLACE_HTML="replace_html";
    /*字段值处理类型-替换掉a标签获得纯文本*/
    public static final String FIELD_PROCESS_TYPE_REPLACE_A="replace_a";
    /*字段值处理类型-截取*/
    public static final String FIELD_PROCESS_TYPE_SUBSTRING_BEFORE="substrbefore";
    public static final String FIELD_PROCESS_TYPE_SUBSTRING_AFTER="substrafter";

    public static final String REGEX_HTML="<[^>]+>";//html标签正则
}
