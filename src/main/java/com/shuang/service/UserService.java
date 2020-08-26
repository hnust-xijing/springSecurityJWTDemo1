package com.shuang.service;

import com.shuang.dao.RolesDao;
import com.shuang.dao.UserDao;
import com.shuang.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @ClassName UserService
 * @Author 6yi
 * @Date 2020/6/21 15:14
 * @Version 1.0
 * @Description:
 */

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private RolesDao rolesDao;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //获取用户
        User user = userDao.selectByUserName(username);
        if(user!=null){
            //获取用户权限信息
            String[] rolesStr = rolesDao.selectByUserName(user.getUsername()).stream().map(h -> h.getRole()).toArray(String[]::new);
            try {
                user.setAuthorities(Arrays.stream(rolesStr).map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return user;
    }
}
