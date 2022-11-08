package com.hys.mylogrecord.config;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 日志记录配置扫描器
 *
 * @author Robert Hou
 * @since 2022年11月08日 10:04
 **/
public class LogRecordConfigurationSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        String annotationName = EnableMyLogRecord.class.getName();
        if (metadata.hasAnnotation(annotationName)) {
            AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(annotationName, true));
            String[] scanBasePackages = (String[]) attributes.get("scanBasePackages");
            EnableMyLogRecordContext.setEnableMyLogRecordScanBasePackagesCache(scanBasePackages);
        }
        return new String[]{LogRecordInitializer.class.getName()};
    }
}
