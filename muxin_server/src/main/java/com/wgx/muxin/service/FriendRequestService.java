package com.wgx.muxin.service;

import com.wgx.muxin.dao.mapper.FriendsRequestMapper;
import com.wgx.muxin.dao.model.FriendsRequest;
import com.wgx.muxin.dao.model.Users;
import com.wgx.muxin.enums.MsgActionEnum;
import com.wgx.muxin.netty.model.DataContent;
import com.wgx.muxin.netty.utils.UserChannelRel;
import com.wgx.muxin.utils.JsonUtils;
import com.wgx.org.n3r.idworker.Sid;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class FriendRequestService {

    @Autowired
    private Sid sid;
    @Autowired
    private MyFriendService myFriendService;
    @Autowired
    private FriendsRequestMapper friendsRequestMapper;
    @Autowired
    private UsersService usersService;

    @Transactional(propagation = Propagation.REQUIRED)
    public void sendAddFriendRequest(String myUserId, String friendUsername){
        Users users = usersService.searchUserByUsername(friendUsername);
        FriendsRequest friendsRequest = new FriendsRequest();
        friendsRequest.setSendUserId(myUserId);
        friendsRequest.setAcceptUserId(users.getId());
        FriendsRequest request = friendsRequestMapper.queryRequest(friendsRequest);
        if (request == null){
            friendsRequest.setId(sid.nextShort());
            friendsRequest.setRequestDateTime(new Date());
            friendsRequestMapper.addRequest(friendsRequest);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void refuseRequest(String acceptUserId, String sendUserId) {
        friendsRequestMapper.deleteRequest(acceptUserId, sendUserId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void passRequest(String acceptUserId, String sendUserId) {
        myFriendService.addFriend(acceptUserId, sendUserId);
        myFriendService.addFriend(sendUserId, acceptUserId);
        refuseRequest(acceptUserId, sendUserId);

        Channel channel = UserChannelRel.get(sendUserId);
        if (channel != null){
            DataContent dataContent = new DataContent();
            dataContent.setAction(MsgActionEnum.PULL_FRIEND.type);
            channel.writeAndFlush(JsonUtils.objectToJson(dataContent));
        }
    }
}
