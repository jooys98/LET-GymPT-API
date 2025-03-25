package com.example.gympt.security.filter;

import com.example.gympt.security.CustomUserDetailService;
import com.example.gympt.security.MemberAuthDTO;
import com.example.gympt.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final CustomUserDetailService userDetailService;

    // 해당 필터로직(doFilterInternal)을 수행할지 여부를 결정하는 메서드
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        log.info("check uri: " + path);

        // Pre-flight 요청은 필터를 타지 않도록 설정
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        // /api/member/로 시작하는 요청은 필터를 타지 않도록 설정
        if (path.startsWith("/api/member/login") || path.startsWith("/api/member/join")
                || path.startsWith("/api/member/refresh") || path.startsWith("/api/member/logout")
                // admin
                || path.startsWith("/api/admin/member/login") || path.startsWith("/api/admin/member/join")
                || path.startsWith("/api/admin/member/refresh") || path.startsWith("/api/admin/member/logout")
        ) {
            return true;
        }


        if (path.startsWith("/api/gym/**")) {
            return true;
        }

        if (path.startsWith("/api/community/**")) {
            return true;
        }

        if (path.startsWith("/api/review/**")) {
            return true;
        }

        // Swagger UI 경로 제외 설정
        if (path.startsWith("/swagger-ui/") || path.startsWith("/v3/api-docs")) {
            return true;
        }
        // h2-console 경로 제외 설정
        if (path.startsWith("/h2-console")) {
            return true;
        }

        // /favicon.ico 경로 제외 설정
        if (path.startsWith("/favicon.ico")) {
            return true;
        }

        //websocket handshake 요청 필터  안타게
        if (path.startsWith("/ws-stomp")) {
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("------------------JWTCheckFilter.................");
        log.info("request.getServletPath(): {}", request.getServletPath());
        log.info("..................................................");

        String autHeaderStr = request.getHeader("Authorization");
        log.info("autHeaderStr Authorization: {}", autHeaderStr);

        if ((Objects.equals(autHeaderStr, "Bearer null") || autHeaderStr == null) && (
                request.getServletPath().startsWith("/api/gym/")
                        //권한 /토큰 없이도 패스 해야 하는 api 경로적어놓기
                        || (request.getServletPath().startsWith("/api/community/") || (request.getServletPath().startsWith("/api/review/") && request.getMethod().equals("GET")
                )))) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Bearer accessToken 형태로 전달되므로 Bearer 제거
            String accessToken = autHeaderStr.substring(7);// Bearer 제거
            // 쿠키로 가져와
//            String accessToken = CookieUtil.getTokenFromCookie(request, "accessToken");
            log.info("JWTCheckFilter accessToken: {}", accessToken);

            Map<String, Object> claims = jwtUtil.validateToken(accessToken);

            log.info("JWT claims: {}", claims);

            MemberAuthDTO memberDTO = (MemberAuthDTO) userDetailService.loadUserByUsername((String) claims.get("email"));

            log.info("memberDTO: {}", memberDTO);
            log.info("memberDto.getAuthorities(): {}", memberDTO.getAuthorities());

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(memberDTO, memberDTO.getPassword(), memberDTO.getAuthorities());

            // SecurityContextHolder에 인증 객체 저장
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // 다음 필터로 이동
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JWT Check Error...........");
            log.error("e.getMessage(): {}", e.getMessage());

            ObjectMapper objectMapper = new ObjectMapper();
            String msg = objectMapper.writeValueAsString(Map.of("error", "ERROR_ACCESS_TOKEN"));

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter printWriter = response.getWriter();
            printWriter.println(msg);
            printWriter.close();
        }


    }
}