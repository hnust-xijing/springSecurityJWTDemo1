package com.shuang.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * roles
 * @author 
 */
@Data
public class Roles implements Serializable {
    private String username;

    private String role;

    private static final long serialVersionUID = 1L;
}