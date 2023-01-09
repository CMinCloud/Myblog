package com.cm.common.aop;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Aspect     //声明为切面类
@Slf4j          //用来创建日志对象
public class LogAspect {

    //    定义切入点
    @Pointcut("@annotation(com.cm.common.aop.LogAnnotation)")  //自定义注解，从而设置路径
    private void logPointCut() {
    }


    //    定义切面
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint pt) throws Throwable {

        long beginTime = System.currentTimeMillis();
        Object result = null;
//        执行方法
        try {
            handleBefore(pt);
            result = pt.proceed();
            handleAfter(result);
        } finally {
//            结束后换行,调用系统定义的换行符，不用写死
            log.info("=======End=======" + System.lineSeparator());
        }
//      计算执行时间
        long time = System.currentTimeMillis() - beginTime;
        log.info("Time expended       : {}ms", time);
//      保存日志
//        recordLog(pt, time);
        return result;
    }

    //    打印方法一：（暂时弃用）
   /* public void recordLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();     //获取执行签名信息
        Method method = signature.getMethod();
        LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);
//        根据注解获取信息打印请求模块及操作
        log.info("=====================log start================================");
        log.info("module:{}", logAnnotation.module());
        log.info("operation:{}", logAnnotation.operation());

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        log.info("request method:{}", className + "." + methodName + "()");

//        //请求的参数
        Object[] args = joinPoint.getArgs();
        String params = JSON.toJSONString(args[0]);
        log.info("params:{}", params);

        //获取request 设置IP地址
//        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
//        log.info("ip:{}", IpUtils.getIpAddr(request));


        log.info("excute time : {} ms", time);
        log.info("=====================log end================================");
    }*/

    //    执行前过程
    public void handleBefore(ProceedingJoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        获取http的请求对象
        HttpServletRequest request = attributes.getRequest();
//        获取方法签名：方法签名包含  注解+controller的整个方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        获取该方法上的LogAnnotation注解对象
        LogAnnotation logAnnotation = getLogAnnotation(joinPoint);

        log.info("=======Start=======");
        // 打印请求 URL
        log.info("URL            : {}", request.getRequestURL());
        // 打印描述信息
        log.info("Operation   : {}", logAnnotation.operation());
        // 打印 Http method
        log.info("HTTP Method    : {}", request.getMethod());
        // 打印调用 controller 的全路径以及执行方法
        log.info("Class Method   : {}.{}", signature.getDeclaringTypeName(), signature.getName());
        // 打印请求的 IP
        log.info("IP             : {}", request.getRemoteHost());
        // 打印请求入参:类型+实参值
        List<Class> classes = Arrays.stream(joinPoint.getArgs())
                .map((Function<Object, Class>) o -> o.getClass()).collect(Collectors.toList());
        log.info("Request ArgsType   : {}", JSON.toJSONString(classes));
        log.info("Request Args   : {}", JSON.toJSONString(joinPoint.getArgs()));
    }

    public LogAnnotation getLogAnnotation(ProceedingJoinPoint joinPoint) {
//        获取Signature的实现类，目的接口是接在方法上的，因此获取方法类型的实现类
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();    //获取被增强的方法
        return method.getAnnotation(LogAnnotation.class);//获取被增强方法的注解对象
    }

    //    打印响应信息
    public void handleAfter(Object result) {
        // 打印出参
        log.info("Response       : {}", result);
    }
}
