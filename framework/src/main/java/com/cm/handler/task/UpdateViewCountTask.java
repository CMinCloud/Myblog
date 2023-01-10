package com.cm.handler.task;


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

    @Scheduled(cron = "0 0/10 * * * ? ")
    public void testJob() {
        //要执行的代码
        redisCache.Synchronize2Database();
        System.out.println("同步浏览量到数据库");
    }
}
