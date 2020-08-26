package com.shuang.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;

/**
 * @ClassName JwtLoginFilter
 * @Author 6yi
 * @Date 2020/6/21 14:16
 * @Version 1.0
 * @Description:  JWT登陆过滤器
 */
public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {
    public final static Algorithm algorithm = Algorithm.HMAC256("ASDFJKFnfsaf");
    public JwtLoginFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager) {
        super(new AntPathRequestMatcher(defaultFilterProcessesUrl));
        setAuthenticationManager(authenticationManager);
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        /**
         * @author 6yi
         * @date 2020/6/21
         * @return
         * @Description 我们从登录参数中提取出用户名密码，然后调用 AuthenticationManager.authenticate() 方法去进行自动校验。
         **/
        String userName=request.getParameter("username");
        String password=request.getParameter("password");
        if(userName==null||password==null){
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.write("请输入帐号和密码");
            out.flush();
            out.close();
        }
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(userName, password));
    }

    @Override
        protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        /**
         * @author 6yi
         * @date 2020/6/21
         * @return
         * @Description 校验成功回调方法
         **/

        //获取用户的权限
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        //拼接字符串
        StringBuffer as = new StringBuffer();
        for (GrantedAuthority authority : authorities) {
            as.append(authority.getAuthority())
                    .append(",");
        }
        //创建token
        String jwtToken = JWT.create()
                //配置用户的角色
                .withClaim("authorities", as.toString())
                .withSubject(authResult.getName())
                //过期时间
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .sign(algorithm);
        //返回token
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write(new ObjectMapper().writeValueAsString(jwtToken));
        out.flush();
        out.close();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        /**
         * @author 6yi
         * @date 2020/6/21
         * @return
         * @Description 校验失败回调
         **/
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write(failed.getMessage());
        out.flush();
        out.close();

    }
}
