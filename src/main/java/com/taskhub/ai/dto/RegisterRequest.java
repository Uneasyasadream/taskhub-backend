package com.taskhub.ai.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String email;
    private String inviteCode;   // 新增邀请码字段
}