package com.wgx.muxin.service;

import com.wgx.muxin.dao.mapper.MyFriendsMapper;
import com.wgx.muxin.dao.model.MyFriends;
import com.wgx.muxin.dao.model.vo.FriendVo;
import com.wgx.org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MyFriendService {

    @Autowired
    private Sid sid;

    @Autowired
    private MyFriendsMapper myFriendsMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    public void addFriend(String acceptUserId, String sendUserId){
        MyFriends friend = new MyFriends();
        friend.setId(sid.nextShort());
        friend.setMyUserId(acceptUserId);
        friend.setMyFriendUserId(sendUserId);
        myFriendsMapper.addFriend(friend);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<FriendVo> myFriendsList(String userId) {
        return myFriendsMapper.myFriendsList(userId);
    }
}
