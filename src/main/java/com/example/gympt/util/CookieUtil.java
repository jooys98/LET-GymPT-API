package com.example.gympt.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;

public class CookieUtil {
    public static void setTokenCookie(HttpServletResponse response, String name, String value, long mins) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/") // CORS 설정, 모든 경로에서 접근 가능, localhost:8080/api에서 path: "/api"
                .httpOnly(true) // XSS 방지, JS에서 쿠키값을 읽는 것을 불가, XSS란?
                .secure(true)   // HTTPS, SSL 설정
                .sameSite("None")  // CORS 설정, 모든 도메인에서 접근 가능, None: 모든 도메인에서 접근 가능, Lax: 일부 도메인에서 접근 가능, Strict: 도메인에서만 접근 가능
                .maxAge(mins * 60) // maxAge 설정 (초)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void removeTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(token, "")
                .path("/")
                .httpOnly(false)
                .secure(true)
                .sameSite("None")
                .maxAge(0L)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static String getTokenFromCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    public static void setNewRefreshTokenCookie(HttpServletResponse response, String refreshToken, String newRefreshToken) {
        ResponseCookie cookie = ResponseCookie.from(refreshToken, newRefreshToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(24 * 60 * 60) // 1day
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
