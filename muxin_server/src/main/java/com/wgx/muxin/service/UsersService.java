package com.wgx.muxin.service;

import com.wgx.muxin.dao.mapper.MyFriendsMapper;
import com.wgx.muxin.dao.mapper.UsersMapper;
import com.wgx.muxin.dao.model.MyFriends;
import com.wgx.muxin.dao.model.Users;
import com.wgx.muxin.dao.model.vo.FriendRequestVo;
import com.wgx.muxin.enums.SearchFriendsStatusEnum;
import com.wgx.muxin.netty.model.ChatMsg;
import com.wgx.muxin.utils.FastDFSClient;
import com.wgx.muxin.utils.FileUtils;
import com.wgx.muxin.utils.MD5Utils;
import com.wgx.muxin.utils.QRCodeUtils;
import com.wgx.org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class UsersService {

    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private MyFriendsMapper myFriendsMapper;
    @Autowired
    private ChatMsgService chatMsgService;
    @Autowired
    private Sid sid;
    @Autowired
    private QRCodeUtils qrCodeUtils;
    @Autowired
    private FastDFSClient fastDFSClient;



    public boolean queryUserNameExist(Users users) {
        Users userInput = new Users();
        userInput.setUsername(users.getUsername());
        Users user = usersMapper.selectOne(userInput);
        return user != null ? true : false;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Users queryUsersForLogin(Users users) throws Exception {
        Users userInput = new Users();
        userInput.setUsername(users.getUsername());
        userInput.setPassword(MD5Utils.getMD5Str(users.getPassword()));
        Users usersResult = usersMapper.queryUsersForLogin(userInput);
        return usersResult;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Users saveUsers(Users user) throws Exception {
        Users userInput = new Users();
        userInput.setId(sid.nextShort());
        userInput.setUsername(user.getUsername());
        userInput.setPassword(MD5Utils.getMD5Str(user.getPassword()));
        userInput.setNickname(user.getUsername());
        userInput.setFaceImage("");
        userInput.setFaceImageBig("");
        // 为用户生成二维码
        String qrCodePath = "C:\\wgx\\" + user.getId() + "qrcode.png";
        // muxin_qrcode:[username]
        qrCodeUtils.createQRCode(qrCodePath, "muxin_qrcode:" + user.getUsername());
        MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrCodePath);

        String qrCodeUrl = "";
        try {
            qrCodeUrl = fastDFSClient.uploadQRCode(qrCodeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        userInput.setQrcode(qrCodeUrl);
        userInput.setCid(user.getCid());
        Integer count = usersMapper.saveUsers(userInput);
        if (count != 0) {
            return userInput;
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Users updateUserInfo(Users user) {
        usersMapper.updateUserInfo(user);
        return getUserById(user.getId());
    }

    private Users getUserById(String id){
        return usersMapper.getUserById(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Users updateUserNicknameByUserId(Users user) {
        usersMapper.updateUserNicknameByUserId(user);
        return getUserById(user.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Integer preConditionSearchFrind(String myUserId, String myFrindName) {
        Users user = searchUserByUsername(myFrindName);

        // 1. 搜索的用户如果不存在，返回[无此用户]
        if (user == null) {
            return SearchFriendsStatusEnum.USER_NOT_EXIST.status;
        }

        // 2. 搜索账号是你自己，返回[不能添加自己]
        if (user.getId().equals(myUserId)) {
            return SearchFriendsStatusEnum.NOT_YOURSELF.status;
        }

        // 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        MyFriends friends = new MyFriends();
        friends.setMyUserId(myUserId);
        friends.setMyFriendUserId(user.getId());
        MyFriends myFriendsRel = myFriendsMapper.selectOneByExample(friends);
        if (myFriendsRel != null) {
            return SearchFriendsStatusEnum.ALREADY_FRIENDS.status;
        }
        return SearchFriendsStatusEnum.SUCCESS.status;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Users searchUserByUsername(String myFrindName){
        return usersMapper.selectOneByUsername(myFrindName);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<FriendRequestVo> queryFriendRequests(String userId) {
        return usersMapper.queryFriendRequests(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String saveChatMsg(ChatMsg chatMessage) {
        return chatMsgService.saveChatMsg(chatMessage);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateMsgSigned(List<String> msgIdList) {
        chatMsgService.updateMsgSigned(msgIdList);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<com.wgx.muxin.dao.model.ChatMsg> getUnReadMsgList(String acceptUserId) {
        return chatMsgService.getUnReadMsgList(acceptUserId);
    }
}
