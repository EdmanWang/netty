package com.wgx.muxin.service;

import com.wgx.muxin.dao.mapper.ChatMsgMapper;
import com.wgx.muxin.enums.MsgSignFlagEnum;
import com.wgx.muxin.netty.model.ChatMsg;
import com.wgx.org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ChatMsgService {

    @Autowired
    private ChatMsgMapper chatMsgMapper;
    @Autowired
    private Sid sid;


    public String saveChatMsg(ChatMsg chatMessage) {
        com.wgx.muxin.dao.model.ChatMsg chatMsg = new com.wgx.muxin.dao.model.ChatMsg();
        chatMsg.setId(sid.nextShort());
        chatMsg.setSendUserId(chatMessage.getSenderId());
        chatMsg.setAcceptUserId(chatMessage.getReceiverId());
        chatMsg.setMsg(chatMessage.getMsg());
        chatMsg.setCreateTime(new Date());
        chatMsg.setSignFlag(MsgSignFlagEnum.unsign.type);
        chatMsgMapper.saveChatMsg(chatMsg);
        return chatMsg.getId();
    }

    public void updateMsgSigned(List<String> msgIdList) {
        chatMsgMapper.updateMsgSigned(msgIdList);
    }


    public List<com.wgx.muxin.dao.model.ChatMsg> getUnReadMsgList(String acceptUserId) {
       return chatMsgMapper.getUnReadMsgList(acceptUserId);
    }
}
