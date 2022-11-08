package com.hys.mylogrecord.config;

import com.hys.mylogrecord.aop.annotation.MyLogRecord;
import com.hys.mylogrecord.customfunction.MyLogRecordFunction;
import com.hys.mylogrecord.customfunction.MyLogRecordSnapshotFunction;
import com.hys.mylogrecord.parse.util.LogRecordParseUtils;
import com.hys.mylogrecord.util.LogRecordConst;
import com.hys.mylogrecord.util.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 日志记录初始化器
 *
 * @author Robert Hou
 * @since 2022年04月24日 17:12
 **/
@Slf4j
public class LogRecordInitializer implements InitializingBean {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        //动态模板初始化
        String[] scanBasePackages = EnableMyLogRecordContext.getEnableMyLogRecordScanBasePackagesCache();
        if (scanBasePackages == null || scanBasePackages.length == 0) {
            scanBasePackages = new String[]{LogRecordConst.ENABLE_MY_LOG_RECORD_DEFAULT_SCAN_BASE_PACKAGES};
        }
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(scanBasePackages)
                .addScanners(new MethodAnnotationsScanner()));
        Set<Method> methods = reflections.getMethodsAnnotatedWith(MyLogRecord.class);
        if (CollectionUtils.isNotEmpty(methods)) {
            log.info("扫描到如下日志记录方法：");
        }
        for (Method method : methods) {
            MyLogRecord annotation = method.getAnnotation(MyLogRecord.class);
            String methodName = method.toGenericString();
            log.info(LogUtils.methodLogDesensitization(methodName));
            buildDynamicTemplate(methodName, annotation);
        }
        //快照初始化
        Set<Class<? extends MyLogRecordSnapshotFunction>> snapshotClasses = reflections.getSubTypesOf(MyLogRecordSnapshotFunction.class);
        initMyLogRecordSnapshotFunctions(snapshotClasses);
        //自定义函数初始化
        Set<Class<? extends MyLogRecordFunction>> classes = reflections.getSubTypesOf(MyLogRecordFunction.class);
        initMyLogRecordFunctions(classes);

        EnableMyLogRecordContext.remove();
    }

    private void buildDynamicTemplate(String methodName, MyLogRecord annotation) {
        //relationId
        String relationId = annotation.relationId();
        LogRecordParseUtils.initDynamicTemplate(methodName, LogRecordConst.RELATION_ID, relationId);
        //operatorId
        String operatorId = annotation.operatorId();
        LogRecordParseUtils.initDynamicTemplate(methodName, LogRecordConst.OPERATOR_ID, operatorId);
        //description
        String description = annotation.description();
        LogRecordParseUtils.initDynamicTemplate(methodName, LogRecordConst.DESCRIPTION, description);
        //snapshot
        String snapshot = annotation.snapshot();
        LogRecordParseUtils.initDynamicTemplate(methodName, LogRecordConst.SNAPSHOT, snapshot);
    }

    private void initMyLogRecordSnapshotFunctions(Set<Class<? extends MyLogRecordSnapshotFunction>> classes) {
        if (CollectionUtils.isEmpty(classes)) {
            return;
        }

        log.info("扫描到如下快照方法：");
        for (Class<? extends MyLogRecordSnapshotFunction> clazz : classes) {
            MyLogRecordSnapshotFunction bean = applicationContext.getBean(clazz);
            log.info(LogUtils.beanLogDesensitization(bean.toString()));
            String functionName = bean.functionName();
            LogRecordParseUtils.validateFunctionName(functionName);
            LogRecordParseUtils.putInitMySnapshotLogRecordFunctions(functionName, bean);
        }
    }

    private void initMyLogRecordFunctions(Set<Class<? extends MyLogRecordFunction>> classes) {
        if (CollectionUtils.isEmpty(classes)) {
            return;
        }

        log.info("扫描到如下自定义函数：");
        for (Class<? extends MyLogRecordFunction> clazz : classes) {
            MyLogRecordFunction bean = applicationContext.getBean(clazz);
            log.info(LogUtils.beanLogDesensitization(bean.toString()));
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
