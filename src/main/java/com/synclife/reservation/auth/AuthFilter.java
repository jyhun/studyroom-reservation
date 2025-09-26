package com.synclife.reservation.auth;

import com.synclife.reservation.enums.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader("Authorization");

        if (token == null) { // 토큰 없는 경우 401 오류
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다.");
            return;
        }

        if (token.equals("admin-token")) {
            request.setAttribute("role", Role.ADMIN);
        } else if (token.startsWith("user-token-")) {
            try {
                long memberId = Long.parseLong(token.substring("user-token-".length()));
                request.setAttribute("role", Role.USER);
                request.setAttribute("memberId", memberId);
            } catch (NumberFormatException e) { // 토큰 형식 잘못된 경우 401 오류
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"잘못된 토큰 형식입니다.");
                return;
            }
        } else { // 알 수 없는 토큰인 경우 401 오류
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"유효하지 않은 토큰입니다.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
