package com.wgx.muxin.netty;

import com.wgx.muxin.enums.MsgActionEnum;
import com.wgx.muxin.netty.model.ChatMsg;
import com.wgx.muxin.netty.model.DataContent;
import com.wgx.muxin.netty.utils.SpringUtil;
import com.wgx.muxin.netty.utils.UserChannelRel;
import com.wgx.muxin.service.UsersService;
import com.wgx.muxin.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CustomerChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    public static ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 获取当前的channel
        Channel currentChannel = ctx.channel();
        // 接收的client数据
        String content = msg.text();

        DataContent dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
        Integer action = dataContent.getAction();
        ChatMsg chatMsg = dataContent.getChatMsg();

        //2.: 对传递过来的action进行判断，做对应的业务处理
        if (action == MsgActionEnum.CONNECT.type){
            // 将senderId和对应的channel联系起来
            String senderId = chatMsg.getSenderId();
            // 将channel 和发送者的用户id联系起来
            UserChannelRel.put(senderId, currentChannel);

            // 测试
            for (Channel c : users) {
                System.out.println(c.id().asLongText());
            }
            UserChannelRel.output();
            // 测试
        }else if (action == MsgActionEnum.CHAT.type){
            // 将数据保存在数据库中, 得到service实例
            UsersService usersService = (UsersService) SpringUtil.getBean("usersService");
            String msgId = usersService.saveChatMsg(chatMsg);
            chatMsg.setMsgId(msgId);

            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setChatMsg(chatMsg);

            // 执行发送消息的操作
            Channel receiverchannel =  UserChannelRel.get(chatMsg.getReceiverId());
            if (receiverchannel == null){ // 伪在线
                // TODO channel为空代表用户离线，推送消息（JPush，个推，小米推送）
            }else {
                // 当receiverChannel不为空的时候，从ChannelGroup去查找对应的channel是否存在
                Channel findChannel = users.find(receiverchannel.id());
                if (findChannel != null) {
                    // 用户在线， 执行发送操作
                    receiverchannel.writeAndFlush(
                            new TextWebSocketFrame(
                                    JsonUtils.objectToJson(dataContentMsg)));
                } else {
                    // 用户离线 TODO 推送消息
                }
            }

        }else if (action == MsgActionEnum.SIGNED.type){
            //  2.3  签收消息类型，针对具体的消息进行签收，修改数据库中对应消息的签收状态[已签收]
            UsersService usersService = (UsersService) SpringUtil.getBean("usersService");
            // 扩展字段在signed类型的消息中，代表需要去签收的消息id，逗号间隔
            String msgIdsStr = dataContent.getExtand();
            String msgIds[] = msgIdsStr.split(",");

            List<String> msgIdList = new ArrayList<>();
            for (String mid : msgIds) {
                if (StringUtils.isNotBlank(mid)) {
                    msgIdList.add(mid);
                }
            }

            System.out.println(msgIdList.toString());

            if (msgIdList != null && !msgIdList.isEmpty() && msgIdList.size() > 0) {
                // 批量签收
                usersService.updateMsgSigned(msgIdList);
            }

        }else if (action == MsgActionEnum.KEEPALIVE.type){
            //  2.4  心跳类型的消息
            System.out.println("收到来自channel为[" + currentChannel + "]的心跳包...");
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 在通道管道组中添加通道
        users.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("管道脱离管道组");
        users.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 答应错误信息
        cause.printStackTrace();
        // 关闭通道
        ctx.channel().close();
        // 移除通道
        users.remove(ctx.channel());
    }
}
