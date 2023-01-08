package com.cm.handler.exception;


import com.cm.domain.vo.ResponseResult;
import com.cm.domain.enums.AppHttpCodeEnum;
import com.cm.domain.entity.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * SSM中的异常处理器
 * 用于捕获Controller层抛出的异常:全局异常处理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

//    捕获自定义异常
    @ExceptionHandler({SystemException.class})
    public ResponseResult systemExceptionHandler(SystemException e){
        //打印异常信息
        log.error("出现了异常！{}", e.getMsg());
        //从异常对象中获取提示信息封装返回
        return ResponseResult.errorResult(e.getCode(),e.getMsg());
    }


////    捕获其他类型的异常
//    @ExceptionHandler(Exception.class)
//    public ResponseResult exceptionHandler(Exception e){
//        //打印异常信息
//        log.error("出现了异常！{}","捕获了未定义类型的异常");
//        //从异常对象中获取提示信息封装返回
//        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR.getCode(),“系统异常”);
//    }

}
