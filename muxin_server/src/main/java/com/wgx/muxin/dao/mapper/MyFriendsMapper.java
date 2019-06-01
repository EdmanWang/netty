package com.wgx.muxin.dao.mapper;

import com.wgx.muxin.dao.model.MyFriends;
import com.wgx.muxin.dao.model.vo.FriendVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MyFriendsMapper {

    MyFriends selectOneByExample(@Param("friends") MyFriends friends);

    Integer addFriend(@Param("friends") MyFriends friends);

    List<FriendVo> myFriendsList(String userId);
}
