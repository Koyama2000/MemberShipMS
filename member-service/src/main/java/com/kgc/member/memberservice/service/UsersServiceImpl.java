package com.kgc.member.memberservice.service;

import com.kgc.member.bean.Users;
import com.kgc.member.bean.UsersExample;
import com.kgc.member.memberservice.mapper.UsersMapper;
import com.kgc.member.service.UsersService;
import com.kgc.member.util.RedisUtil;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import jodd.util.StringUtil;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsersServiceImpl implements UsersService {
    @Resource
    UsersMapper usersMapper;
    @Resource
    JestClient jestClient;
    @Resource
    RedissonClient redissonClient;
    @Resource
    RedisUtil redisUtil;
    
    @Override
    public List<Users> USERS_LIST(String username) {
        List<Users> list=new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();
        if(StringUtil.isNotBlank(username)){
            MatchQueryBuilder matchQueryBuilder=new MatchQueryBuilder("name",username);
            boolQueryBuilder.must(matchQueryBuilder);
        }
        searchSourceBuilder.query(boolQueryBuilder);
        String dsl=searchSourceBuilder.toString();
        Search search=new Search.Builder(dsl).addIndex("users").addType("usersinfo").build();
        try {
            SearchResult searchResult=jestClient.execute(search);
            List<SearchResult.Hit<Users,Void>> hits=searchResult.getHits(Users.class);
            for (SearchResult.Hit<Users,Void> hit: hits){
                list.add(hit.source);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Users> USERS_LIST(String username, Integer pageNum, Integer pageSize) {
        List<Users> list=new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();
        if(StringUtil.isNotBlank(username)){
            MatchQueryBuilder matchQueryBuilder=new MatchQueryBuilder("name",username);
            boolQueryBuilder.must(matchQueryBuilder);
        }
        searchSourceBuilder.from((pageNum-1)*pageSize);
        searchSourceBuilder.query(boolQueryBuilder);
        String dsl=searchSourceBuilder.toString();
        Search search=new Search.Builder(dsl).addIndex("users").addType("usersinfo").build();
        try {
            SearchResult searchResult=jestClient.execute(search);
            List<SearchResult.Hit<Users,Void>> hits=searchResult.getHits(Users.class);
            for (SearchResult.Hit<Users,Void> hit: hits){
                list.add(hit.source);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Users LOGIN(String username, String password) {
        UsersExample usersExample=new UsersExample();
        UsersExample.Criteria criteria=usersExample.createCriteria();
        criteria.andNameEqualTo(username);
        List<Users> users = usersMapper.selectByExample(usersExample);
        if(users.size()>0){
            if(users.get(0).getPassword().equals(password)){
               return users.get(0);
            }
            return null;
        }
        return null;
    }
    @Resource
    EsService esService;

    @Override
    public int UPDATE_PWD(Integer id, String password, String newpassword) {
        Users users=usersMapper.selectByPrimaryKey(id);
        if(users!=null){
            if (!users.getPassword().equals(password)) {
                return 3;
            }
            users.setPassword(newpassword);
            int i = usersMapper.updateByPrimaryKeySelective(users);
            if(i>0){
                String index =String.valueOf(users.getId());
                try {
                    esService.deleteData(index,"users","usersinfo");
                    Index in=new Index.Builder(users).index("users").type("usersinfo").id(users.getId()+"").build();
                    jestClient.execute(in);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return i;
            }
        }
        return 0;
    }
}
