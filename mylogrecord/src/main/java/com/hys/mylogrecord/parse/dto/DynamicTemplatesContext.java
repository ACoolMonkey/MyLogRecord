package com.hys.mylogrecord.parse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 动态模板上下文
 *
 * @author Robert Hou
 * @since 2022年04月23日 19:31
 **/
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DynamicTemplatesContext implements Serializable {

    private static final long serialVersionUID = -607118061613716260L;

    private String annotation;

    private String content;

    private List<DynamicTemplate> templates;

    public String getResult() {
        if (CollectionUtils.isEmpty(templates)) {
            return content;
        }

        StringBuilder result = new StringBuilder();
        int size = templates.size();
        for (int i = 0; i < size; i++) {
            DynamicTemplate template = templates.get(i);
            Integer startIndex = template.getStartIndex();
            if (i != 0) {
                DynamicTemplate lastTemplate = templates.get(i - 1);
                result.append(content, lastTemplate.getEndIndex() + 2, startIndex - 1);
            } else {
                result.append(content, 0, startIndex - 1);
            }
            result.append(template.getParsedTemplate());
        }
        result.append(content.substring(templates.get(templates.size() - 1).getEndIndex() + 2));
        return result.toString();
    }
}
