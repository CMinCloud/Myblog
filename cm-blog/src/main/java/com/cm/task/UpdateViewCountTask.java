package com.cm.task;


import com.cm.common.aop.LogAnnotation;
import com.cm.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling   // 开启定时任务
public class UpdateViewCountTask {

    @Autowired
    private RedisCache redisCache;

//    每10分钟更新一次
    @Scheduled(cron = "0 0/10 * * * ? ")
//    @LogAnnotation(module = "定时任务",operation = "同步缓存浏览量到数据库")
    public void testJob() {
        //要执行的代码`
        redisCache.Synchronize2Database();
        System.out.println("同步浏览量到数据库");
    }
}
