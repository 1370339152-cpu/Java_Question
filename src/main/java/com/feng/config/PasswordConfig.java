package com.feng.config;

import com.feng.utils.IdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author feng
 * @function: 用户登录功能算法配置
 */
@Configuration
public class PasswordConfig {
    /*
     * 密码加密器
     * */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * 雪花id生成器
     * */
    @Bean
    public IdWorker idWorker() {
        return new IdWorker();
    }
}
