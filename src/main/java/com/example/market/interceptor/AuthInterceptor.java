package com.example.market.interceptor;

import com.example.market.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtutil;
    
    public AuthInterceptor(JwtUtil jwtutil){
        this.jwtutil = jwtutil;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        Long userId = (token == null) ? null :jwtutil.parseUserId(token);
        if(userId == null){
            response.setStatus(401);  //未登录
            return false;
        }
        request.setAttribute("userId",userId);  //传给controller用
        return true;
    }
}
