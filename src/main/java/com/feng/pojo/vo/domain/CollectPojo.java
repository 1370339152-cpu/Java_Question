package com.feng.pojo.vo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author feng
 * @function: 收藏功能响应数据封装类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollectPojo {
    //收藏题目的ID
    private Integer questionId;
    //题目内容
    private String question;
    //收藏时间
    private String createTime;
}
