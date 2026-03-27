package com.feng.pojo.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author feng
 * @function: 忘记密码响应数据封装类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForgetRespVo {
    //账号
    private String username;
    //真实姓名
    private String realname;
    //真实密码
    private String password;
}
