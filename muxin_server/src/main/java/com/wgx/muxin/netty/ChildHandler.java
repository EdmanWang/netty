package com.wgx.muxin.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class ChildHandler extends ChannelInitializer {

    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();

        // 添加相关的助手类
        pipeline.addLast("HttpServerCodec", new HttpServerCodec());
        // 大数据流
        pipeline.addLast("ChunkedWriteHandler", new ChunkedWriteHandler());
        // 聚合流 ,即使对httpRequest 和httpResponse 进行封装
        pipeline.addLast("HttpObjectAggregator", new HttpObjectAggregator(1024 * 6));
        // 添加路由器
        pipeline.addLast("WebSocketServerProtocolHandler", new WebSocketServerProtocolHandler("/ws"));


        // ====================== 增加心跳支持 start    ======================
        // 针对客户端，如果在1分钟时没有向服务端发送读写心跳(ALL)，则主动断开
        // 如果是读空闲或者写空闲，不处理
        pipeline.addLast(new IdleStateHandler(8, 10, 60));
        // 自定义的空闲状态检测
        pipeline.addLast(new HeartBeatHandler());

        // 自定义拦截器（助手类）
        pipeline.addLast("CustomerChatHandler", new CustomerChatHandler());
    }
}
