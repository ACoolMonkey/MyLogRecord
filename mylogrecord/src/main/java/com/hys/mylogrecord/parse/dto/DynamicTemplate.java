package com.hys.mylogrecord.parse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 动态模板
 *
 * @author Robert Hou
 * @since 2022年04月23日 19:59
 **/
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DynamicTemplate implements Serializable {

    private static final long serialVersionUID = 4711886054872477103L;

    private String template;

    private Integer startIndex;

    private Integer endIndex;

    private String parsedTemplate;

    private Boolean parsed;
}
