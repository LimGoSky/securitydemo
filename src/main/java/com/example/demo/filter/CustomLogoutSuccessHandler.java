package com.example.demo.filter;

import com.example.demo.service.TokenServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义退出处理类 返回成功
 */
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    private final TokenServiceImpl tokenService;

    public CustomLogoutSuccessHandler(TokenServiceImpl tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * 退出处理
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException{

        String jwtToken = request.getHeader("authorization");
        String username = tokenService.parseToken(jwtToken).getSubject();
        tokenService.del(jwtToken);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(username + "退出成功。。。");
    }
}