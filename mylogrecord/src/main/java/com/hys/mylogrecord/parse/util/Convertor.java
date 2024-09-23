package com.hys.mylogrecord.parse.util;

import com.hys.mylogrecord.parse.dto.DynamicTemplate;
import com.hys.mylogrecord.parse.dto.DynamicTemplatesContext;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Robert Hou
 * @since 2024年09月23日 18:55
 **/
@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface Convertor {

    Convertor INSTANCE = Mappers.getMapper(Convertor.class);

    @Mapping(target = "templates", expression = "java(buildTemplates(dynamicTemplatesContext))")
    DynamicTemplatesContext dynamicTemplatesContextDeepCopy(DynamicTemplatesContext dynamicTemplatesContext);

    default List<DynamicTemplate> buildTemplates(DynamicTemplatesContext dynamicTemplatesContext) {
        if (dynamicTemplatesContext == null || CollectionUtils.isEmpty(dynamicTemplatesContext.getTemplates())) {
            return Collections.emptyList();
        }

        return dynamicTemplatesContext.getTemplates().stream().map(this::dynamicTemplateDeepCopy).collect(Collectors.toList());
    }

    DynamicTemplate dynamicTemplateDeepCopy(DynamicTemplate dynamicTemplate);
}
