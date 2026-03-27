package com.feng.pojo.vo.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author feng
 * @function: 用户请求封装类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginReqVo {
    //用户账号
    private String username;
    //用户密码
    private String password;
    //验证码
    private String captcha;
    //会话ID
    private String sessionId;
    //记住我
    private String remember;

}
