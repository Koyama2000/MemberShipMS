package com.kgc.member.memberservice.service;

import com.kgc.member.bean.UserType;
import com.kgc.member.bean.UserType;
import com.kgc.member.memberservice.mapper.UserTypeMapper;
import com.kgc.member.service.UserTypeService;
import com.kgc.member.util.RedisUtil;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import jodd.util.StringUtil;
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
public class UserTypeServiceImpl implements UserTypeService {
    @Resource
    UserTypeMapper userTypeMapper;
    @Resource
    JestClient jestClient;
    @Resource
    RedissonClient redissonClient;
    @Resource
    RedisUtil redisUtil;
    
    @Override
    public List<UserType> USER_TYPE_LIST() {
        List<UserType> list=new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();
        searchSourceBuilder.query(boolQueryBuilder);
        String dsl=searchSourceBuilder.toString();
        Search search=new Search.Builder(dsl).addIndex("usetype").addType("usertypeinfo").build();
        try {
            SearchResult searchResult=jestClient.execute(search);
            List<SearchResult.Hit<UserType,Void>> hits=searchResult.getHits(UserType.class);
            for (SearchResult.Hit<UserType,Void> hit: hits){
                list.add(hit.source);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
