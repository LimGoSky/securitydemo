package com.example.demo.service;

import com.example.demo.bean.Permission;
import com.example.demo.bean.Role;
import com.example.demo.bean.UserInfo;
import com.example.demo.repository.UserInfoRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class CustomUserDetailServiceImpl implements UserDetailsService{

    private final UserInfoRepository userInfoRepository;

    public CustomUserDetailServiceImpl(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++");
        //通过username获取用户信息
        UserInfo userInfo = userInfoRepository.findByUsername(username);
        if(userInfo == null) {
            throw new UsernameNotFoundException("not found");
        }

        //定义权限列表.
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for(Role role:userInfo.getRoles()){
            for (Permission perm:role.getPermissions()){
                authorities.add(new SimpleGrantedAuthority(perm.getUrl()));
            }
        }

        User userDetails = new User(username,userInfo.getPassword(),authorities);
        System.out.println("当前用户拥有权限："+userDetails.getAuthorities());
        return userDetails;
    }
}
