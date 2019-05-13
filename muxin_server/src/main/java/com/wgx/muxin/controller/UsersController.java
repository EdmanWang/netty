package com.wgx.muxin.controller;

import com.wgx.muxin.dao.model.Users;
import com.wgx.muxin.dao.model.vo.UsersVo;
import com.wgx.muxin.service.UsersService;
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


@RestController
@RequestMapping("user")
public class UsersController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UsersService usersService;

    @PostMapping("/userRegistOrLogin")
    public JSONResult userRegistOrLogin(@RequestBody Users users) throws Exception {

        if (StringUtils.isBlank(users.getUsername())) {
            JSONResult.errorMsg("用户名不能为空！！！！");
        }
        boolean isExist = usersService.queryUserNameExist(users);

        Users user = null;
        UsersVo userResult = new UsersVo();
        if (isExist) {
            user = usersService.queryUsersForLogin(users);
            if (user == null) {
                JSONResult.errorMsg("用户名或者密码不对！！！！");
            }
        } else {
            user = usersService.saveUsers(users);
            if (user == null) {
                JSONResult.errorMsg("用户注册失败！！！！");
            }
        }
        BeanUtils.copyProperties(user, userResult);

        return JSONResult.ok(userResult);
    }
}
