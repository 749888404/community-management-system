package com.community.entity;

import lombok.Data;

/**
 * 登录请求参数
 */
@Data
public class LoginDTO {
    private String username;   // 登录账号
    private String password;   // 登录密码
}
