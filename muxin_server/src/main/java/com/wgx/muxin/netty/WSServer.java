package com.wgx.muxin.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

// 聊天服务器
public class WSServer {

    private static class singletionWSServer {
        private static WSServer instance = new WSServer();
    }

    public static WSServer getInstance() {
        return singletionWSServer.instance;
    }

    private EventLoopGroup parentGroup;
    private EventLoopGroup childGroup;
    private ServerBootstrap serverBootstrap;
    private ChannelFuture channelFuture;

    public WSServer() {
        parentGroup = new NioEventLoopGroup();
        childGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(parentGroup, childGroup).channel(NioServerSocketChannel.class).childHandler(new ChildHandler());
    }

    public void start() {
        channelFuture = serverBootstrap.bind(8088);
        System.err.println("服务器启动完成--------------");
    }
}
