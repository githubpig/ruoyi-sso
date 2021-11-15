package com.ruoyi.common.utils.thread.test;

import cn.hutool.core.thread.ThreadUtil;
import com.ruoyi.common.utils.thread.MultiThreadHandler;
import com.ruoyi.common.utils.thread.exception.ChildThreadException;
import com.ruoyi.common.utils.thread.parallel.ParallelTaskWithThreadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class Test {

    public static void main(String[] args) {
        List<String> result = new ArrayList<>();
        ExecutorService service = ThreadUtil.newExecutor(3);
        MultiThreadHandler handler = new ParallelTaskWithThreadPool(service);
        Runnable task = null;
        // 启动3个子线程作为要处理的并行任务，共同完成结果集resultMap
        for(int i=0;i<12;i++){
          final   String s=i+"";
            task = new Runnable() {
                @Override
                public void run() {
                    System.out.println("当前执行线程：" + s);
                    result.add("第"+s+"个任务");
                }
            };
            handler.addTask(task);
            /*ThreadUtil.execute(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("第"+s+"个线程");
                result.add("第"+s+"个任务");
            });*/
        }
        try {
            handler.run();
        } catch (ChildThreadException e) {
            e.printStackTrace();
        }
        service.shutdown();
        for(String s:result){
            System.out.println(s);
        }
    }

}
