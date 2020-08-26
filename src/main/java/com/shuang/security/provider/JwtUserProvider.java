package com.shuang.security.provider;

import com.shuang.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

/**
 * @ClassName JwtUserProvider
 * @Author 6yi
 * @Date 2020/6/21 14:37
 * @Version 1.0
 * @Description:
 */

@Component
public class JwtUserProvider extends AbstractUserDetailsAuthenticationProvider {

    @Autowired
    UserService userService;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
            return userService.loadUserByUsername(username);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken userToken=(UsernamePasswordAuthenticationToken)authentication;
        UserDetails userDetails = retrieveUser(userToken.getPrincipal().toString(), userToken);
        /*
        *
        *   这里可以追加各种验证,例如用户是否被Ban等等
        *
        *
        * */
        if(userDetails==null){
            throw new BadCredentialsException("NotFound this userName");
        }
        boolean isPassword= BCrypt.checkpw(userToken.getCredentials().toString(),userDetails.getPassword());
        if(isPassword){
            //返回一个新的UsernamePasswordAuthenticationToken
            return  createSuccessAuthentication(userDetails.getUsername(),authentication,userDetails);
        }else{
            throw new BadCredentialsException("Invalid username/password");
        }


    }
}
