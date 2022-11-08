package com.hys.mylogrecord.aop;

import com.google.common.collect.Maps;
import com.hys.mylogrecord.aop.annotation.MyLogRecord;
import com.hys.mylogrecord.parse.util.LogRecordParseUtils;
import com.hys.mylogrecord.persistence.LogRecordFactory;
import com.hys.mylogrecord.util.LogRecordContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

/**
 * 日志记录切面
 *
 * @author Robert Hou
 * @since 2022年04月22日 00:53
 **/
@Aspect
@Component
@Slf4j
public class LogRecordAspect {

    @Autowired
    private LogRecordFactory logRecordFactory;

    @Pointcut("@annotation(com.hys.mylogrecord.aop.annotation.MyLogRecord)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();
        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        Object[] paramValues = joinPoint.getArgs();
        Map<String, Object> paramNamesValues = paramNamesValues(paramNames, paramValues);
        MyLogRecord annotation = method.getAnnotation(MyLogRecord.class);
        //初始化缓存
        LogRecordParseUtils.initDynamicTemplatesTL(method.toGenericString());
        Object proceed;
        try {
            //快照解析
            LogRecordParseUtils.executeLogRecordSnapshotFunctions(paramNamesValues);
            //自定义函数预执行
            LogRecordParseUtils.executeLogRecordFunctions(true, paramNamesValues);

            ////////////////////////////////////////////////// 业务方法 start //////////////////////////////////////////////////
            proceed = joinPoint.proceed();
            ////////////////////////////////////////////////// 业务方法 end //////////////////////////////////////////////////

            //自定义函数后执行 & SpEL解析
            LogRecordParseUtils.executeLogRecordFunctions(false, paramNamesValues);
        } catch (Throwable throwable) {
            LogRecordParseUtils.remove();
            LogRecordContext.remove();
            throw throwable;
        }
        //持久化
        try {
            logRecordFactory.record(annotation);
        } catch (Exception e) {
            log.error("日志持久化出错！", e);
        }
        //清除ThreadLocal缓存
        LogRecordParseUtils.remove();
        LogRecordContext.remove();

        return proceed;
    }

    private Map<String, Object> paramNamesValues(String[] paramNames, Object[] paramValues) {
        if (paramNames == null || paramValues == null) {
            return Collections.emptyMap();
        }

        int length = paramNames.length;
        Map<String, Object> map = Maps.newHashMapWithExpectedSize(length);
        for (int i = 0; i < length; i++) {
            map.put(paramNames[i], paramValues[i]);
        }
        return map;
    }
}
