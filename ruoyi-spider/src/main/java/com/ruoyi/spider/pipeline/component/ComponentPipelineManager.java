package com.ruoyi.spider.pipeline.component;

import com.ruoyi.common.utils.spring.SpringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class ComponentPipelineManager {

    @Autowired
    List<Pipeline> componentPipelines;

    public void setComponentPipelines(List<Pipeline> componentPipelines) {
        this.componentPipelines = componentPipelines;
    }

    private static List<String> pipelineList=new ArrayList<>();
    @PostConstruct
    public void init() {
        if (CollectionUtils.isNotEmpty(componentPipelines)) {
            for (Pipeline pipeline : componentPipelines) {
                Class cls = pipeline.getClass();
                Object instance = null;
                if (!cls.getName().contains("proxy")) {
                    instance = SpringUtils.getBean(cls);
                    if (instance instanceof Pipeline) {
                        pipelineList.add(instance.getClass().getSimpleName());
                    }
                }
            }
        }
    }
    public static List<String> getPipelineList(){
        return pipelineList;
    }
}
