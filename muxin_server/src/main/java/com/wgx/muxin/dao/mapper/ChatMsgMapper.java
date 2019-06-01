package com.wgx.muxin.dao.mapper;

import com.wgx.muxin.dao.model.ChatMsg;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ChatMsgMapper{
    void saveChatMsg(@Param("chatMsg") ChatMsg chatMsg);

    void updateMsgSigned(List<String> msgIdList);

    List<ChatMsg> getUnReadMsgList(String acceptUserId);
}
