package com.example.demo.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class TokenServiceImpl {
    public Integer expiration;
    public String signingKey;

    final StringRedisTemplate redisTemplate;

    public TokenServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String createToken(String username, Collection<? extends GrantedAuthority> authorities){
        String jwtToken= Jwts.builder()
                //存入用户权限信息
                .claim("authorities", authorities)
                .setSubject(username)
                //失效时间
                .setExpiration(new Date(System.currentTimeMillis() + expiration*1000))
                //签名算法和密钥
                .signWith(SignatureAlgorithm.HS512,signingKey)
                .compact();
        //将jwtToken存入redis
        if(set(jwtToken,jwtToken)){
            return jwtToken;
        }else {
            return null;
        }
    }

    /**
     *  获取存入jwt的个人变量authorities。
     */
    public Collection<GrantedAuthority> getAuthority(Claims claims){
        Collection<GrantedAuthority> authorities=new ArrayList<>();
        String[] as=claims.get("authorities").toString().replace("[","").replace("]","").split(",");
        for (String a:as){
            authorities.add(new SimpleGrantedAuthority(a));
        }
        return authorities;
    }

    /**
     *  解析redis中存的jwtToken
     */
    public Claims parseToken(String jwtToken){
        String token = get(jwtToken.replace("Bearer","").trim());
        Claims claims =null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(signingKey)
                    .parseClaimsJws(token)
                    .getBody();
        }catch (Exception e){
            e.printStackTrace();
        }
        return claims;
    }

    /**
     * 下面的方法对redis进行操作，包括存、取和删。
     */
    private boolean set(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value,expiration, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    public boolean del(String key) {
        try {
            redisTemplate.delete(key.replace("Bearer","").trim());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
