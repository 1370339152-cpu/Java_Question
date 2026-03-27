package com.feng.pojo.vo.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author feng
 * @function: 忘记密码请求数据封装类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForgetReqVo {
    //账户
    private String username;
    //真实姓名
    private String realname;
    //验证码
    private String code;
    //会话id
    private String sessionId;
}
