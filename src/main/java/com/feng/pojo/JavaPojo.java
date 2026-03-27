package com.feng.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author feng
 * @function: 题目封装类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JavaPojo {
    //题目序列
    private Integer id;
    //题目名称
    private String question;
}
