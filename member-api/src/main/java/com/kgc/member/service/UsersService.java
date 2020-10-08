package com.kgc.member.service;

import com.kgc.member.bean.Users;

import java.util.List;

public interface UsersService {
    public List<Users> USERS_LIST(String username);
    public List<Users> USERS_LIST(String username,Integer pageNum,Integer pageSize);
    public Users LOGIN(String username,String password);
    public int UPDATE_PWD(Integer id,String password,String newpassword);
}
