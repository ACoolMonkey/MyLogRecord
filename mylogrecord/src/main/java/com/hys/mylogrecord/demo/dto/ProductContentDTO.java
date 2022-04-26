package com.hys.mylogrecord.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Robert Hou
 * @since 2022年04月26日 20:33
 **/
@Data
@NoArgsConstructor
public class ProductContentDTO implements Serializable {

    private static final long serialVersionUID = 7551275615681345893L;

    private String content;
}
