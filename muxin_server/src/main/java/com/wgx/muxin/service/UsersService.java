package com.wgx.muxin.service;

import com.wgx.muxin.dao.mapper.UsersMapper;
import com.wgx.muxin.dao.model.Users;
import com.wgx.muxin.utils.MD5Utils;
import com.wgx.org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private Sid sid;

    public boolean queryUserNameExist(Users users) {
        Users userInput = new Users();
        userInput.setUsername(users.getUsername());
        Users user = usersMapper.selectOne(userInput);
        return user != null ? true : false;
    }

    public Users queryUsersForLogin(Users users) throws Exception {
        Users userInput = new Users();
        userInput.setUsername(users.getUsername());
        userInput.setPassword(MD5Utils.getMD5Str(users.getPassword()));
        Users usersResult = usersMapper.queryUsersForLogin(userInput);
        return usersResult;
    }

    public Users saveUsers(Users user) throws Exception {
        Users userInput = new Users();
        userInput.setId(sid.nextShort());
        userInput.setUsername(user.getUsername());
        userInput.setPassword(MD5Utils.getMD5Str(user.getPassword()));
        userInput.setNickname(user.getUsername());
        userInput.setFaceImage("");
        userInput.setFaceImageBig("");
        userInput.setQrcode("");
        userInput.setCid(user.getCid());
        Integer count = usersMapper.saveUsers(userInput);
        if (count != 0) {
            return userInput;
        }
        return null;
    }
}
