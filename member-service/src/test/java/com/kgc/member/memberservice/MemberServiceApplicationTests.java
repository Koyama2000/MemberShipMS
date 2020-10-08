package com.kgc.member.memberservice;

        import com.kgc.member.bean.UserType;
        import com.kgc.member.bean.Users;
        import com.kgc.member.memberservice.mapper.UserTypeMapper;
        import com.kgc.member.memberservice.mapper.UsersMapper;
        import io.searchbox.client.JestClient;
        import io.searchbox.core.Index;
        import org.junit.jupiter.api.Test;
        import org.springframework.beans.BeanUtils;
        import org.springframework.boot.test.context.SpringBootTest;

        import javax.annotation.Resource;
        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.List;

@SpringBootTest
class MemberServiceApplicationTests {
    @Resource
    UsersMapper usersMapper;
    @Resource
    UserTypeMapper userTypeMapper;
    @Resource
    JestClient jestClient;
    @Test
    void contextLoads() {
        List<UserType> allusertype = userTypeMapper.selectByExample(null);
        System.out.println("projectinfolist:"+allusertype);
        List<UserType> userTypes=new ArrayList<>();
        for (UserType userType : allusertype) {
            UserType userType1 = new UserType();
            BeanUtils.copyProperties(userType,userType1);
            userTypes.add(userType1);
        }
        System.out.println(userTypes);
        for (UserType userType : userTypes) {
            Index index=new Index.Builder(userType).index("usetype").type("usertypeinfo").id(userType.getId()+"").build();
            try {
                jestClient.execute(index);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void contextLoadss() {
        List<Users> allusers = usersMapper.selectByExample(null);
        System.out.println("allusers:"+allusers);
        for (Users users : allusers) {
            Index index=new Index.Builder(users).index("users").type("usersinfo").id(users.getId()+"").build();
            try {
                jestClient.execute(index);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
