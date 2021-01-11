package com.example.demo.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * 权限验证类
 */
@Component
public class PermissionServiceImpl {
    final TokenServiceImpl tokenService;

    public PermissionServiceImpl(TokenServiceImpl tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * 验证用户是否具备某权限
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public boolean hasPermission(String permission){
        System.out.println("..................................................");
        if (StringUtils.isEmpty(permission)){
            return false;
        }

        //获取当前登录用户的jwtToken。
        String jwtToken = getRequest().getHeader("authorization");
        System.out.println(jwtToken);
        //直接从jwt中解析用户权限，避免重复查询数据库。
        Collection<GrantedAuthority> authorities=tokenService.getAuthority(tokenService.parseToken(jwtToken));

        //封装当前用户拥有的权限
        Collection<String> permissions=new ArrayList<>();
        for(GrantedAuthority authority:authorities){
            String perm=authority.getAuthority().split("=")[1].replace("}","");
            permissions.add(perm);
        }
        System.out.println("当前需要权限："+permission);
        System.out.println("当前用户权限："+permissions);
        return permissions.contains(permission);
    }

    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }
}
