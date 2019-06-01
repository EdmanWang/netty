package com.wgx.muxin.dao.mapper;

import com.wgx.muxin.dao.model.FriendsRequest;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface FriendsRequestMapper {
    FriendsRequest queryRequest(@Param("friendsRequest") FriendsRequest friendsRequest);

    void addRequest(@Param("friendsRequest") FriendsRequest friendsRequest);

    void deleteRequest(@Param("acceptUserId") String acceptUserId, @Param("sendUserId") String sendUserId);
}
