package com.base;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {

    @Test
    public void testPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String rawPassword = "admin123";
        String storedHash = "$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/TU.qj6wClLG";

        // 验证存储的哈希是否匹配
        boolean matches = encoder.matches(rawPassword, storedHash);
        System.out.println("密码 'admin123' 与存储的哈希匹配: " + matches);

        // 生成新的哈希
        String newHash = encoder.encode(rawPassword);
        System.out.println("新生成的哈希: " + newHash);

        // 验证新哈希
        boolean newMatches = encoder.matches(rawPassword, newHash);
        System.out.println("新哈希验证: " + newMatches);
    }

    @Test
    public void testJwtSecretLength() {
        // HS512 需要至少 512 位 = 64 字节
        String secret1 = "base-system-secret-key-2026-01-12-very-long-secret-key";
        String secret2 = "base-system-secret-key-2026-01-12-very-long-secret-key-must-be-at-least-512-bits-for-hs512-algorithm-security";

        System.out.println("Secret1 长度: " + secret1.length() + " 字符, " + (secret1.length() * 8) + " 位");
        System.out.println("Secret2 长度: " + secret2.length() + " 字符, " + (secret2.length() * 8) + " 位");
        System.out.println("HS512 需要至少 64 字符 (512 位)");

        // 生成一个足够长的密钥
        String validSecret = "base-system-jwt-secret-key-2026-for-hs512-algorithm-must-be-64-chars-or-more-1234567890";
        System.out.println("有效密钥长度: " + validSecret.length() + " 字符, " + (validSecret.length() * 8) + " 位");
        System.out.println("有效密钥: " + validSecret);
    }
}
