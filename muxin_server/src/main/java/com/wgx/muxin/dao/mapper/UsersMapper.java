package com.wgx.muxin.dao.mapper;

import com.wgx.muxin.dao.model.Users;
import com.wgx.muxin.dao.model.vo.FriendRequestVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UsersMapper {
    /**
     * 根据uses中封装的查询条件，去查询相应的用户信息
     */
    Users selectOne(@Param("users") Users users);

    /**
     * 根据uses中封装的查询条件，去查询相应的用户信息
     */
    Users selectOneByUsername(@Param("username") String username);

    /**
     * 根据uses中封装的查询条件，去查询相应的用户信息
     */
    Users queryUsersForLogin(@Param("users") Users users);

    /**
     * 用户注册
     */
    int saveUsers(@Param("users") Users users);

    void updateUserInfo(@Param("users") Users user);

    Users getUserById(String id);

    void updateUserNicknameByUserId(@Param("users") Users user);

    List<FriendRequestVo> queryFriendRequests(String userId);
}
