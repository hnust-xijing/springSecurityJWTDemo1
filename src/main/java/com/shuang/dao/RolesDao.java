package com.shuang.dao;

import com.shuang.domain.Roles;

import java.util.List;

public interface RolesDao {
    int insert(Roles record);

    int insertSelective(Roles record);

    List<Roles> selectByUserName(String userName);
}