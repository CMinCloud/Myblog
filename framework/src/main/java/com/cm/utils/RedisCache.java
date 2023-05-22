package com.cm.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cm.domain.entity.Article;
import com.cm.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings(value = {"unchecked", "rawtypes"})
@Component
public class RedisCache {
    @Autowired
    public RedisTemplate redisTemplate;

    @Autowired
    private ArticleService articleService;


    //    用于存入到缓存中的key
    public static final String ViewCountKey = "articleViewCount";

    //    用于将管理员路由缓存的key
    public static final String AdminRouterKey = "AdminRouters:";

    //    添加粉丝id到博主的缓存中
    public static final String BlogFansKey = "BlogFans:";

    //    添加博客id到用户的缓存中
    public static final String BlogFeedKey = "BlogFeed:";

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public long deleteObject(final Collection collection) {
        return redisTemplate.delete(collection);
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> long setCacheList(final String key, final List<T> dataList) {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet) {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext()) {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public <T> Set<T> getCacheSet(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<String, T> getCacheMap(final String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getCacheMapValue(final String key, final String hKey) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 删除Hash中的数据
     *
     * @param key
     * @param hkey
     */
    public void delCacheMapValue(final String key, final String hkey) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(key, hkey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys) {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }

    //    将数据库中文章的浏览量存入缓存
    public void setSortedSet(List<Article> articles) {
        String key = "articleViewCount";
        for (Article article : articles) {
            redisTemplate.opsForZSet().add(key, article.getId(), article.getViewCount());
        }
    }

    //    讲单篇文章的浏览量写入缓存
    public void setViewCount2Redis(Article article) {
        String key = "articleViewCount";
        redisTemplate.opsForZSet().add(key, article.getId(), article.getViewCount());
    }

    //    从redis中获取当前文章的浏览量
    public Long getSetSortedSetById(Long id) {
        String key = "articleViewCount";
        Double score = redisTemplate.opsForZSet().score(key, String.valueOf(id));
        long viewCount = score.longValue();
        return viewCount;
    }

    //    更新redis中获取当前文章的浏览量
    public boolean updateSetSortedSet(Long id) {
        String key = "articleViewCount";
//        已存在的会直接更新
        return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, id, getSetSortedSetById(id) + 1));
    }

    //        将缓存中的浏览量同步到数据库
    public void Synchronize2Database() {
        Cursor cursor = redisTemplate.opsForZSet().scan(ViewCountKey, ScanOptions.NONE);
//        这里对redis中每一篇文章都进行遍历，效果不太好
        while (cursor.hasNext()) {
            ZSetOperations.TypedTuple typedTuple = (ZSetOperations.TypedTuple) cursor.next();
            String value = typedTuple.getValue().toString();//文章id
            Double score = typedTuple.getScore();//浏览量
            UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
//            根据主键来修改，防止行级锁升级为表级锁
            updateWrapper.eq("id", Long.valueOf(value)).set("view_count", score.longValue());
            articleService.update(updateWrapper);
        }
    }

    public void addFans2Redis(Long userId, Long fansId) {
        String key = BlogFansKey + userId;
        redisTemplate.opsForSet().add(key, fansId);
    }

    public void deleteFans4Redis(Long userId, Long fansId) {
        String key = BlogFansKey + userId;
        redisTemplate.opsForSet().remove(key, fansId);
    }

    //    使用zset的格式，增加一个更新时间
    public void addFollowArticle2Redis(Long userId, Long articleId) {
        String key = BlogFeedKey + userId;
        redisTemplate.opsForZSet().add(key, articleId, System.currentTimeMillis());
    }

    public Set getFollowArticle4Redis(Long userId) {
        String key = BlogFeedKey + userId;
//        redisTemplate.opsForZSet().reverseRange(key, 0, 5); // 倒叙查询出五个
        Cursor cursor = redisTemplate.opsForZSet().scan(key, ScanOptions.NONE);
//        这里对redis中每一篇文章都进行遍历，效果不太好
        Set<String> articleIds = new HashSet<>();
        while (cursor.hasNext()) {
            ZSetOperations.TypedTuple typedTuple = (ZSetOperations.TypedTuple) cursor.next();
            articleIds.add(typedTuple.getValue().toString());//文章id
//            根据主键来修改，防止行级锁升级为表级锁
        }
        return articleIds;
    }

    /**
     * 取关后，将被关注者的所有文章id查出来，删除原粉丝推送缓存中的所有文章
     *
     * @param userId
     * @param articleIds
     */
    public void deleteFollowArticle4Redis(Long userId, List<Long> articleIds) {
        String key = BlogFeedKey + userId;
        redisTemplate.opsForZSet().remove(key, articleIds);
    }
}