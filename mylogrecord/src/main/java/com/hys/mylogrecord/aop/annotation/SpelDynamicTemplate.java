package com.hys.mylogrecord.aop.annotation;

import java.lang.annotation.*;

/**
 * 支持SpEL动态模板
 *
 * @author Robert Hou
 * @since 2022年04月23日 16:38
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpelDynamicTemplate {
}
