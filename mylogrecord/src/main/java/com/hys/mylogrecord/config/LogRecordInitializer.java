package com.hys.mylogrecord.config;

import com.hys.mylogrecord.aop.annotation.MyLogRecord;
import com.hys.mylogrecord.customfunction.MyLogRecordFunction;
import com.hys.mylogrecord.customfunction.MyLogRecordSnapshotFunction;
import com.hys.mylogrecord.parse.util.LogRecordParseUtils;
import com.hys.mylogrecord.util.LogRecordUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 日志记录初始化器
 *
 * @author Robert Hou
 * @since 2022年04月24日 17:12
 **/
@Component
public class LogRecordInitializer implements InitializingBean {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        //动态模板初始化
        Reflections reflections = new Reflections("com.hys", new MethodAnnotationsScanner(), new SubTypesScanner());
        Set<Method> methods = reflections.getMethodsAnnotatedWith(MyLogRecord.class);
        for (Method method : methods) {
            MyLogRecord annotation = method.getAnnotation(MyLogRecord.class);
            buildDynamicTemplate(method.toGenericString(), annotation);
        }
        //快照初始化
        Set<Class<? extends MyLogRecordSnapshotFunction>> snapshotClasses = reflections.getSubTypesOf(MyLogRecordSnapshotFunction.class);
        initMyLogRecordSnapshotFunctions(snapshotClasses);
        //自定义函数初始化
        Set<Class<? extends MyLogRecordFunction>> classes = reflections.getSubTypesOf(MyLogRecordFunction.class);
        initMyLogRecordFunctions(classes);
    }

    private void buildDynamicTemplate(String methodName, MyLogRecord annotation) {
        //relationId
        String relationId = annotation.relationId();
        LogRecordParseUtils.initDynamicTemplate(methodName, LogRecordUtils.RELATION_ID, relationId);
        //operatorId
        String operatorId = annotation.operatorId();
        LogRecordParseUtils.initDynamicTemplate(methodName, LogRecordUtils.OPERATOR_ID, operatorId);
        //description
        String description = annotation.description();
        LogRecordParseUtils.initDynamicTemplate(methodName, LogRecordUtils.DESCRIPTION, description);
        //snapshot
        String snapshot = annotation.snapshot();
        LogRecordParseUtils.initDynamicTemplate(methodName, LogRecordUtils.SNAPSHOT, snapshot);
    }

    private void initMyLogRecordSnapshotFunctions(Set<Class<? extends MyLogRecordSnapshotFunction>> classes) {
        if (CollectionUtils.isEmpty(classes)) {
            return;
        }

        for (Class<? extends MyLogRecordSnapshotFunction> clazz : classes) {
            MyLogRecordSnapshotFunction bean = applicationContext.getBean(clazz);
            String functionName = bean.functionName();
            LogRecordParseUtils.validateFunctionName(functionName);
            LogRecordParseUtils.putInitMySnapshotLogRecordFunctions(functionName, bean);
        }
    }

    private void initMyLogRecordFunctions(Set<Class<? extends MyLogRecordFunction>> classes) {
        if (CollectionUtils.isEmpty(classes)) {
            return;
        }

        for (Class<? extends MyLogRecordFunction> clazz : classes) {
            MyLogRecordFunction bean = applicationContext.getBean(clazz);
            String functionName = bean.functionName();
            LogRecordParseUtils.validateFunctionName(functionName);
            boolean executeBefore = bean.executeBefore();
            if (executeBefore) {
                LogRecordParseUtils.putInitMyExecuteBeforeLogRecordFunctions(functionName, bean);
            } else {
                LogRecordParseUtils.putInitMyExecuteAfterLogRecordFunctions(functionName, bean);
            }
        }
    }
}