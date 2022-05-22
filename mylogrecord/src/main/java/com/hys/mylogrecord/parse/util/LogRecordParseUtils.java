package com.hys.mylogrecord.parse.util;

import com.google.common.collect.Sets;
import com.hys.mylogrecord.customfunction.MyLogRecordFunction;
import com.hys.mylogrecord.customfunction.MyLogRecordSnapshotFunction;
import com.hys.mylogrecord.exception.LogRecordParseException;
import com.hys.mylogrecord.parse.dto.BracketsIndexStack;
import com.hys.mylogrecord.parse.dto.DynamicTemplate;
import com.hys.mylogrecord.parse.dto.DynamicTemplatesContext;
import com.hys.mylogrecord.util.LogRecordConst;
import com.hys.mylogrecord.util.LogRecordUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日志记录解析工具类
 *
 * @author Robert Hou
 * @since 2022年04月23日 19:40
 **/
public class LogRecordParseUtils {

    private static final Pattern SPEL_PATTERN = Pattern.compile("^#([\\w$]+)?(\\..+)?$", Pattern.CASE_INSENSITIVE);

    private static final Map<String, Set<DynamicTemplatesContext>> INIT_DYNAMIC_TEMPLATES = new HashMap<>();
    private static final ThreadLocal<Set<DynamicTemplatesContext>> DYNAMIC_TEMPLATES = ThreadLocal.withInitial(HashSet::new);

    private static final Map<String, MyLogRecordFunction> MY_EXECUTE_BEFORE_LOG_RECORD_FUNCTIONS = new HashMap<>();
    private static final Map<String, MyLogRecordFunction> MY_EXECUTE_AFTER_LOG_RECORD_FUNCTIONS = new HashMap<>();
    private static final Map<String, MyLogRecordSnapshotFunction> MY_SNAPSHOT_LOG_RECORD_FUNCTIONS = new HashMap<>();

    private static final Pattern FUNCTION_NAME_PATTERN = Pattern.compile("^[\\w$]+$", Pattern.CASE_INSENSITIVE);
    private static final Pattern CUSTOM_FUNCTION_PATTERN = Pattern.compile("^([\\w$]+)?\\{(#([\\w$]+)?(\\..+)?)\\}$", Pattern.CASE_INSENSITIVE);

    private static final SpelExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser();

    private LogRecordParseUtils() {
    }

    public static void remove() {
        DYNAMIC_TEMPLATES.remove();
    }

    private static void setDynamicTemplatesParsed(String annotation, List<DynamicTemplate> templates) {
        Set<DynamicTemplatesContext> dynamicTemplatesContextTL = DYNAMIC_TEMPLATES.get();
        if (dynamicTemplatesContextTL == null) {
            return;
        }
        DynamicTemplatesContext selected = dynamicTemplatesContextTL.stream().filter(item -> item.getAnnotation().equals(annotation)).findFirst().orElse(null);
        if (selected == null) {
            return;
        }
        selected.setTemplates(templates);
        DYNAMIC_TEMPLATES.set(dynamicTemplatesContextTL);
    }

    public static DynamicTemplatesContext getDynamicTemplates(String annotation) {
        Set<DynamicTemplatesContext> dynamicTemplatesContextTL = DYNAMIC_TEMPLATES.get();
        if (dynamicTemplatesContextTL == null) {
            return null;
        }
        return dynamicTemplatesContextTL.stream().filter(item -> item.getAnnotation().equals(annotation)).findFirst().orElse(null);
    }

    public static void initDynamicTemplatesTL(String methodName) {
        doInitDynamicTemplatesTL(methodName, LogRecordConst.RELATION_ID);
        doInitDynamicTemplatesTL(methodName, LogRecordConst.OPERATOR_ID);
        doInitDynamicTemplatesTL(methodName, LogRecordConst.DESCRIPTION);
        doInitDynamicTemplatesTL(methodName, LogRecordConst.SNAPSHOT);
    }

    private static void doInitDynamicTemplatesTL(String methodName, String annotation) {
        if (MapUtils.isEmpty(INIT_DYNAMIC_TEMPLATES)) {
            return;
        }
        Set<DynamicTemplatesContext> dynamicTemplateContexts = INIT_DYNAMIC_TEMPLATES.get(methodName);
        if (CollectionUtils.isEmpty(dynamicTemplateContexts)) {
            return;
        }
        DynamicTemplatesContext selected = dynamicTemplateContexts.stream().filter(item -> item.getAnnotation().equals(annotation)).findFirst().orElse(null);
        if (selected == null) {
            return;
        }
        DynamicTemplatesContext dynamicTemplatesContextCopy = deepCopy(selected);
        Set<DynamicTemplatesContext> dynamicTemplatesContextTL = DYNAMIC_TEMPLATES.get();
        if (dynamicTemplatesContextTL == null) {
            dynamicTemplatesContextTL = Sets.newHashSetWithExpectedSize(1);
        }
        dynamicTemplatesContextTL.add(dynamicTemplatesContextCopy);
        DYNAMIC_TEMPLATES.set(dynamicTemplatesContextTL);
    }

    /**
     * 深拷贝
     */
    private static <T> T deepCopy(T src) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            @SuppressWarnings("unchecked")
            T dest = (T) in.readObject();
            return dest;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void initDynamicTemplate(String methodName, String annotation, String content) {
        if (StringUtils.isBlank(methodName) || StringUtils.isBlank(content) || !content.contains(LogRecordConst.LEFT_BRACKET_STR) || !content.contains(LogRecordConst.RIGHT_BRACKET_STR)) {
            return;
        }

        //拆分动态模板
        DynamicTemplatesContext dynamicTemplatesContext = buildDynamicTemplate(annotation, content);
        Set<DynamicTemplatesContext> existContextDynamicTemplates = INIT_DYNAMIC_TEMPLATES.get(methodName);
        if (existContextDynamicTemplates == null) {
            existContextDynamicTemplates = Sets.newHashSetWithExpectedSize(1);
        }
        existContextDynamicTemplates.add(dynamicTemplatesContext);
        INIT_DYNAMIC_TEMPLATES.put(methodName, existContextDynamicTemplates);
    }

    private static DynamicTemplatesContext buildDynamicTemplate(String annotation, String content) {
        BracketsIndexStack bracketsIndexStack = new BracketsIndexStack();
        List<DynamicTemplate> dynamicTemplates = new ArrayList<>(2);
        int length = content.length();
        for (int i = 0; i < length; i++) {
            if (content.charAt(i) == LogRecordConst.LEFT_BRACKET) {
                bracketsIndexStack.push(i);
            } else if (content.charAt(i) == LogRecordConst.RIGHT_BRACKET) {
                Integer startIndex = bracketsIndexStack.pop();
                if (bracketsIndexStack.isEmpty()) {
                    int start = startIndex + 1;
                    String template = content.substring(start, i);
                    DynamicTemplate dynamicTemplate = DynamicTemplate.builder().template(template).startIndex(start).endIndex(i - 1).parsedTemplate(template).parsed(false).build();
                    dynamicTemplates.add(dynamicTemplate);
                }
            }
        }
        if (!bracketsIndexStack.isEmpty()) {
            throw new LogRecordParseException("动态模板格式有误！请排查！" + content);
        }
        return DynamicTemplatesContext.builder().annotation(annotation).content(content).templates(dynamicTemplates).build();
    }

    private static Object getSpelTemplateResult(String template, Map<String, Object> paramNamesValues) {
        Matcher matcher = SPEL_PATTERN.matcher(template);
        if (!matcher.find()) {
            return null;
        }
        String functionName = matcher.group(1);
        Object paramValue = paramNamesValues.get(functionName);
        String suffix = matcher.group(2);
        String expressionString;
        if (suffix == null) {
            expressionString = "#root";
        } else {
            expressionString = "#root" + suffix;
        }
        Expression expression = SPEL_EXPRESSION_PARSER.parseExpression(expressionString);
        return expression.getValue(paramValue);
    }

    public static void putInitMyExecuteBeforeLogRecordFunctions(String functionName, MyLogRecordFunction myLogRecordFunction) {
        MY_EXECUTE_BEFORE_LOG_RECORD_FUNCTIONS.put(functionName, myLogRecordFunction);
    }

    public static void putInitMyExecuteAfterLogRecordFunctions(String functionName, MyLogRecordFunction myLogRecordFunction) {
        MY_EXECUTE_AFTER_LOG_RECORD_FUNCTIONS.put(functionName, myLogRecordFunction);
    }

    public static void putInitMySnapshotLogRecordFunctions(String functionName, MyLogRecordSnapshotFunction myLogRecordSnapshotFunction) {
        MY_SNAPSHOT_LOG_RECORD_FUNCTIONS.put(functionName, myLogRecordSnapshotFunction);
    }

    public static void validateFunctionName(String functionName) {
        Matcher matcher = FUNCTION_NAME_PATTERN.matcher(functionName);
        if (!matcher.find()) {
            throw new LogRecordParseException("方法名不符合规范（^[\\w$]+$）！" + functionName);
        }
    }

    public static void executeLogRecordFunctions(boolean executeBefore, Map<String, Object> paramNamesValues) {
        if ((executeBefore && MapUtils.isEmpty(MY_EXECUTE_BEFORE_LOG_RECORD_FUNCTIONS))) {
            return;
        }

        //relationId
        DynamicTemplatesContext relationIdDT = getDynamicTemplates(LogRecordConst.RELATION_ID);
        doExecuteLogRecordFunctions(executeBefore, relationIdDT, LogRecordConst.RELATION_ID, paramNamesValues);
        //operatorId
        DynamicTemplatesContext operatorIdDT = getDynamicTemplates(LogRecordConst.OPERATOR_ID);
        doExecuteLogRecordFunctions(executeBefore, operatorIdDT, LogRecordConst.OPERATOR_ID, paramNamesValues);
        //description
        DynamicTemplatesContext descriptionDT = getDynamicTemplates(LogRecordConst.DESCRIPTION);
        doExecuteLogRecordFunctions(executeBefore, descriptionDT, LogRecordConst.DESCRIPTION, paramNamesValues);
    }

    public static void executeLogRecordSnapshotFunctions(Map<String, Object> paramNamesValues) {
        if (MapUtils.isEmpty(MY_SNAPSHOT_LOG_RECORD_FUNCTIONS)) {
            return;
        }

        DynamicTemplatesContext snapshotDT = getDynamicTemplates(LogRecordConst.SNAPSHOT);
        doExecuteLogRecordSnapshotFunctions(snapshotDT, paramNamesValues);
    }

    private static void doExecuteLogRecordFunctions(boolean executeBefore, DynamicTemplatesContext dynamicTemplatesContext, String annotation, Map<String, Object> paramNamesValues) {
        if (dynamicTemplatesContext == null) {
            return;
        }

        List<DynamicTemplate> templates = dynamicTemplatesContext.getTemplates();
        if (CollectionUtils.isEmpty(templates)) {
            return;
        }
        for (DynamicTemplate template : templates) {
            String innerTemplate = template.getTemplate();
            if (executeBefore && !isContainsCustomFunction(innerTemplate)) {
                //注：executeBefore=true是自定义函数独有的功能，SpEL不具备这个功能
                continue;
            }
            if (BooleanUtils.isTrue(template.getParsed())) {
                //已经解析过的不再解析
                continue;
            }
            Object templateResult = getTemplateResult(executeBefore, innerTemplate, paramNamesValues);
            if (templateResult == null) {
                template.setParsedTemplate("");
            } else {
                template.setParsedTemplate(String.valueOf(templateResult));
                template.setParsed(true);
            }
        }
        setDynamicTemplatesParsed(annotation, templates);
    }

    private static void doExecuteLogRecordSnapshotFunctions(DynamicTemplatesContext dynamicTemplatesContext, Map<String, Object> paramNamesValues) {
        if (dynamicTemplatesContext == null) {
            return;
        }

        List<DynamicTemplate> templates = dynamicTemplatesContext.getTemplates();
        if (CollectionUtils.isEmpty(templates)) {
            return;
        }
        for (DynamicTemplate template : templates) {
            String innerTemplate = template.getTemplate();
            Object templateResult = getSnapshotTemplateResult(innerTemplate, paramNamesValues);
            LogRecordUtils.addSnapshotCache(templateResult);
        }
    }

    private static boolean isContainsCustomFunction(String template) {
        Matcher matcher = CUSTOM_FUNCTION_PATTERN.matcher(template);
        return matcher.find();
    }

    private static Object getTemplateResult(boolean executeBefore, String template, Map<String, Object> paramNamesValues) {
        if (template.startsWith(LogRecordConst.WELL_NUMBER) && executeBefore) {
            //注：executeBefore=true是自定义函数独有的功能，SpEL不具备这个功能
            return null;
        } else if (template.startsWith(LogRecordConst.WELL_NUMBER) && !executeBefore) {
            //如果是“#”开头的参数，直接解析SpEL表达式
            return getSpelTemplateResult(template, paramNamesValues);
        }
        //否则，尝试解析自定义函数
        Matcher matcher = CUSTOM_FUNCTION_PATTERN.matcher(template);
        if (!matcher.find()) {
            return null;
        }
        Map<String, MyLogRecordFunction> myLogRecordFunctionMap;
        if (executeBefore) {
            myLogRecordFunctionMap = MY_EXECUTE_BEFORE_LOG_RECORD_FUNCTIONS;
        } else {
            myLogRecordFunctionMap = MY_EXECUTE_AFTER_LOG_RECORD_FUNCTIONS;
        }
        String functionName = matcher.group(1);
        MyLogRecordFunction myLogRecordFunction = myLogRecordFunctionMap.get(functionName);
        if (myLogRecordFunction == null) {
            return null;
        }
        String parameter = matcher.group(2);
        Object parameterResult = getSpelTemplateResult(parameter, paramNamesValues);
        //回调自定义函数
        String result = myLogRecordFunction.apply(parameterResult);
        if (result == null) {
            result = "";
        }
        return result;
    }

    private static Object getSnapshotTemplateResult(String template, Map<String, Object> paramNamesValues) {
        if (template.startsWith(LogRecordConst.WELL_NUMBER)) {
            //如果是“#”开头的参数，直接解析SpEL表达式
            return getSpelTemplateResult(template, paramNamesValues);
        }
        //否则，尝试解析自定义函数
        Matcher matcher = CUSTOM_FUNCTION_PATTERN.matcher(template);
        if (!matcher.find()) {
            return null;
        }
        String functionName = matcher.group(1);
        MyLogRecordSnapshotFunction myLogRecordSnapshotFunction = MY_SNAPSHOT_LOG_RECORD_FUNCTIONS.get(functionName);
        if (myLogRecordSnapshotFunction == null) {
            return null;
        }
        String parameter = matcher.group(2);
        Object parameterResult = getSpelTemplateResult(parameter, paramNamesValues);
        //回调自定义函数
        return myLogRecordSnapshotFunction.snapshotApply(parameterResult);
    }
}
