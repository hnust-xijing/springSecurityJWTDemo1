package com.shuang.dao;

import com.shuang.domain.User;

public interface UserDao {
    int insert(User record);


    int insertSelective(User record);

    User selectByUserName(String userName);

}