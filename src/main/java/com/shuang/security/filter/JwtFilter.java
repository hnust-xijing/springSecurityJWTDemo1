package com.shuang.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName JwtFilter
 * @Author 6yi
 * @Date 2020/6/21 14:31
 * @Version 1.0
 * @Description:
 */

public class JwtFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = req.getHeader("authorization");
        if(jwtToken==null){
            resp.setContentType("application/json;charset=utf-8");
            PrintWriter out = resp.getWriter();
            out.write("请登陆!");
            out.flush();
            out.close();
            return;
        }
        JWTVerifier verifier = JWT.require(JwtLoginFilter.algorithm).build();
        DecodedJWT decode;
        try {
            verifier.verify(jwtToken);
            decode = JWT.decode(jwtToken);
        }catch (Exception e){
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("token 失效");
            return;
        }
        //获取当前登录用户名
        String username = decode.getSubject();
        //获取角色权限
        List<GrantedAuthority> authorities = Arrays.stream(decode.getClaim("authorities").asString().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        //擦除密码信息
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, authorities);
        //将token放在threadloacl里
        token.setDetails(new WebAuthenticationDetails(req));
        SecurityContextHolder.getContext().setAuthentication(token);
        filterChain.doFilter(req,resp);
    }
}
