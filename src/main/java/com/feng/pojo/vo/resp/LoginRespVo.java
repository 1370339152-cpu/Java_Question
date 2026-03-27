package com.feng.pojo.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author feng
 * @function: 用户响应封装类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRespVo {
    //用户id
    private Long id;
    //用户账户
    private String username;
    //用户密码
    private String password;
    //用户姓名
    private String realname;
}
