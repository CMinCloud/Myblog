package com.cm.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;
public class BeanCopyUtils {

    //    使用单例模式，将构造器私有化
    private BeanCopyUtils() {
    }

    /**
     * @param source 拷贝的对象
     * @param clazz  拷贝的结果
     * @return
     */
    public static <T> T copyBean(Object source, Class<T> clazz) {
//        创建目标对象
        T result = null;
        try {
//            这里运用到了反射：根据空参构造器创建对象
            result = clazz.newInstance();   //拷贝的结果存放在class类型的对象当中
//        实现属性copy
            BeanUtils.copyProperties(source, result);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
//        返回结果
        return result;
    }

    /**
     * 拷贝一个集合
     *
     * @param sourceList
     * @param clazz
     * @param <V>
     * @param <T>
     * @return
     */
    public static <V, T> List<T> copyBeanList(List<V> sourceList, Class<T> clazz) {
/*        sourceList.stream().map(new Function<V, Object>() {
            @Override
            public Object apply(V v) {
                return copyBean(v, clazz);
            }
        }).collect(Collectors.toList());*/
//        使用lambada表达式
        return sourceList.stream().map(v -> copyBean(v, clazz)).collect(Collectors.toList());
    }


}
