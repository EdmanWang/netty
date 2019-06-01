package com.wgx.muxin.netty.utils;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

// 将用户id和对应的channel 联系起来
public class UserChannelRel {

    public static HashMap<String, Channel> manage = new HashMap<>();

    public static void put(String userId, Channel channel){
        manage.put(userId, channel);
    }

    public static Channel get(String userId){
        return manage.get(userId);
    }

    public static void output(){
        for (Map.Entry<String, Channel> entry : manage.entrySet()){
            System.out.println("用户的id是"+entry.getKey()+"对应的channel是"+entry.getValue().id().asLongText());
        }
    }
}
