package com.wgx.muxin.controller;

import com.wgx.muxin.dao.mapper.MyFriendsMapper;
import com.wgx.muxin.dao.model.ChatMsg;
import com.wgx.muxin.dao.model.MyFriends;
import com.wgx.muxin.dao.model.Users;
import com.wgx.muxin.dao.model.bo.UsersBO;
import com.wgx.muxin.dao.model.vo.FriendRequestVo;
import com.wgx.muxin.dao.model.vo.FriendVo;
import com.wgx.muxin.dao.model.vo.UsersVo;
import com.wgx.muxin.enums.OperatorFriendRequestTypeEnum;
import com.wgx.muxin.enums.SearchFriendsStatusEnum;
import com.wgx.muxin.service.FriendRequestService;
import com.wgx.muxin.service.MyFriendService;
import com.wgx.muxin.service.UsersService;
import com.wgx.muxin.utils.FastDFSClient;
import com.wgx.muxin.utils.FileUtils;
import com.wgx.muxin.utils.JSONResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("u")
public class UsersController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UsersService usersService;
    @Autowired
    private FastDFSClient fastDFSClient;
    @Autowired
    private FriendRequestService friendRequestService;
    @Autowired
    private MyFriendService myFriendService;


    @PostMapping("/registOrLogin")
    public JSONResult userRegistOrLogin(@RequestBody Users users) throws Exception {
        System.out.println("用户登录---注册");

        if (StringUtils.isBlank(users.getUsername())) {
            JSONResult.errorMsg("用户名不能为空！！！！");
        }
        boolean isExist = usersService.queryUserNameExist(users);

        Users user = null;
        // 返回到前端的实体类
        UsersVo userResult = new UsersVo();
        if (isExist) {
            // 用户直接登录
            user = usersService.queryUsersForLogin(users);
            if (user == null) {
                JSONResult.errorMsg("用户名或者密码不对！！！！");
            }
        } else {
            // 用户注册
            user = usersService.saveUsers(users);
            if (user == null) {
                JSONResult.errorMsg("用户注册失败！！！！");
            }
        }
        // beanUtils 是springBoot的工具类
        BeanUtils.copyProperties(user, userResult);

        return JSONResult.ok(userResult);
    }

    /**
     * @Description: 上传用户头像
     */
    @PostMapping("/uploadFaceBase64")
    public JSONResult uploadFaceBase64(@RequestBody UsersBO userBO) throws Exception {

        // 获取前端传过来的base64字符串, 然后转换为文件对象再上传
        String base64Data = userBO.getFaceData();

        String userFacePath = "C:\\wgx\\" + userBO.getUserId() + "userface64.png";
        File tempFile = new File(userFacePath);
        if(!tempFile.getParentFile().exists()){
            tempFile.getParentFile().mkdirs();//创建父级文件路径
            tempFile.createNewFile();//创建文件
            System.out.println(tempFile.exists());
        }

        FileUtils.base64ToFile(userFacePath, base64Data);

        // 上传文件到fastdfs
        MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
        String url = fastDFSClient.uploadBase64(faceFile);
        System.out.println(url);

        // 获取缩略图的url
        String thump = "_80x80.";
        String arr[] = url.split("\\.");
        String thumpImgUrl = arr[0] + thump + arr[1];

        // 更细用户头像
        Users user = new Users();
        user.setId(userBO.getUserId());
        user.setFaceImage(thumpImgUrl);
        user.setFaceImageBig(url);

        Users result = usersService.updateUserInfo(user);

        return JSONResult.ok(result);
    }

    /**
     * @Description: 修改用户昵称
     */
    @PostMapping("/updateUserNicknameByUserId")
    public JSONResult updateUserNicknameByUserId(@RequestBody UsersBO userBO) {
        Users user = new Users();
        user.setId(userBO.getUserId());
        user.setNickname(userBO.getNickname());

        Users result = usersService.updateUserNicknameByUserId(user);

        return JSONResult.ok(result);
    }

    /**
     * @Description: 用户搜索好友
     */
    @PostMapping("/search")
    public JSONResult serchFrind(String myUserId, String myFrindName) {
        if (StringUtils.isBlank(myUserId)
                || StringUtils.isBlank(myFrindName)) {
            return JSONResult.errorMsg("");
        }

        Integer status = usersService.preConditionSearchFrind(myUserId, myFrindName);
        if (status == SearchFriendsStatusEnum.SUCCESS.status) {
            Users user = usersService.searchUserByUsername(myFrindName);
            UsersVo userVO = new UsersVo();
            BeanUtils.copyProperties(user, userVO);
            return JSONResult.ok(userVO);
        } else {
            String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return JSONResult.errorMsg(errorMsg);
        }
    }

    /**
     * @Description: 添加朋友请求
     * @param myUserId
     */
    @PostMapping("/addFriendRequest")
    public JSONResult addFriendRequest(String myUserId, String friendUsername) {
        if (StringUtils.isBlank(myUserId) || StringUtils.isBlank(friendUsername)) {
            return JSONResult.errorMsg("");
        }
        Integer status = usersService.preConditionSearchFrind(myUserId, friendUsername);
        if (status == SearchFriendsStatusEnum.SUCCESS.status) {
            friendRequestService.sendAddFriendRequest(myUserId, friendUsername);
        }
        return JSONResult.ok();
    }

    /**
     * @Description: 添加朋友请求
     * @param userId
     */
    @PostMapping("/queryFriendRequests")
    public JSONResult queryFriendRequests(String userId){
        if (StringUtils.isBlank(userId) ) {
            return JSONResult.errorMsg("");
        }
        List<FriendRequestVo> friendRequestVoList =  usersService.queryFriendRequests(userId);
        return JSONResult.ok(friendRequestVoList);
    }

    /**
     * @Description: 处理添加的好友请求
     */
    @PostMapping("/operFriendRequest")
    public JSONResult operFriendRequest(String acceptUserId, String sendUserId, Integer operType){
        if (StringUtils.isBlank(acceptUserId) || StringUtils.isBlank(sendUserId) || operType == null) {
            return JSONResult.errorMsg("");
        }
        if (OperatorFriendRequestTypeEnum.getMsgByType(operType) == null){
            return JSONResult.errorMsg("");
        }

        if (OperatorFriendRequestTypeEnum.IGNORE.getType() == operType){
            friendRequestService.refuseRequest(acceptUserId, sendUserId);
        }else if (OperatorFriendRequestTypeEnum.PASS.getType() == operType){
            friendRequestService.passRequest(acceptUserId, sendUserId);
        }
        List<FriendVo> friendVoList = myFriendService.myFriendsList(acceptUserId);
        return JSONResult.ok();
    }

    /**
     * @Description: 显示用户的好友列表
     * @param userId
     */
    @PostMapping("/myFriends")
    public JSONResult myFriends(String userId){
        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("");
        }
        List<FriendVo> friendVoList = myFriendService.myFriendsList(userId);
        return JSONResult.ok(friendVoList);
    }

    /**
     * @Description: 用户手机端获取未签收的消息列表
     * @param acceptUserId
     */
    @PostMapping("/getUnReadMsgList")
    public JSONResult getUnReadMsgList(String acceptUserId){
        // 0. userId 判断不能为空
        if (StringUtils.isBlank(acceptUserId)) {
            return JSONResult.errorMsg("");
        }

        // 查询列表
        List<ChatMsg> unreadMsgList = usersService.getUnReadMsgList(acceptUserId);

        return JSONResult.ok(unreadMsgList);
    }
}
