package com.kgc.member.memberweb.controller;

import com.kgc.member.bean.UserType;
import com.kgc.member.bean.Users;
import com.kgc.member.service.UserTypeService;
import com.kgc.member.service.UsersService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UsersController {
    @Reference
    UsersService usersService;
    @Reference
    UserTypeService userTypeService;


    @RequestMapping("/usertype/list")
    @ResponseBody
    public List<UserType> userTypeList(){
        return userTypeService.USER_TYPE_LIST();
    }
    @RequestMapping("/users/list")
    @ResponseBody
    public Map<String,Object> UsersList(@RequestParam(value = "name",required = false,defaultValue = "")String name,
                                        @RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum,
                                        @RequestParam(value = "pageSize",required = false,defaultValue = "3")Integer pageSize){
        Map<String,Object> map=new HashMap<>();
        List<Users> usersList=usersService.USERS_LIST(name);
        if(usersList.size()==0){
            map.put("pages",0);
            map.put("prePage",0);
            map.put("nextPage",0);
            map.put("count",0);
            map.put("navigatepageNums",null);
            map.put("hasPrePage",0);
            map.put("hasNextPage",0);
            map.put("pageinfo",null);
            return map;
        }
        Integer count=usersList.size();
        Integer pages=count%pageSize==0?count/pageSize:count/pageSize+1;
        if(pageNum<1){
            pageNum=1;
        }
        if(pageNum>pages){
            pageNum=pages;
        }
        List<Users> pagelist=usersService.USERS_LIST(name, pageNum, pageSize);
        System.out.println(pagelist);
        map.put("pages",pages);
        map.put("prePage",pageNum-1);
        map.put("nextPage",pageNum+1);
        map.put("count",count);
        List<Integer> navigatepageNums=new ArrayList<>();
        for (int i=1;i<=pages;i++){
            navigatepageNums.add(i);
        }
        map.put("navigatepageNums",navigatepageNums);
        map.put("hasPrePage",pageNum>1);
        map.put("hasNextPage",pageNum<pages);
        map.put("pageinfo",pagelist);
        return map;
    }

    @RequestMapping("/login")
    @ResponseBody
    public Map<String,Object> login(@RequestParam(value = "username",required = false,defaultValue = "") String username,
                       @RequestParam(value = "password",required = false,defaultValue = "")String password, HttpSession session){
        Map<String,Object> map=new HashMap<>();
        Users login = usersService.LOGIN(username, password);
        if(login!=null){
            session.setAttribute("user",login);
            map.put("status","OK");
            return map;
        }else{
            map.put("status","NO");
            return map;
        }
    }

    @RequestMapping("/user/update")
    @ResponseBody
    public int update(Integer id,String password,String newpassword){
        int i = usersService.UPDATE_PWD(id, password, newpassword);
        return i;
    }

    @RequestMapping("/user/session")
    @ResponseBody
    public Users sessionusers(HttpSession session){
        Users user =(Users) session.getAttribute("user");
        return user;
    }
}
