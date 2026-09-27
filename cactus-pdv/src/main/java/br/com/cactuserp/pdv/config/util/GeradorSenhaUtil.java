package br.com.cactuserp.pdv.config.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeradorSenhaUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder(10);
        System.out.println("ger@123 → " + enc.encode("ger@123"));
        System.out.println("sup@123 → " + enc.encode("sup@123"));
        System.out.println("1234    → " + enc.encode("1234"));
    }
}