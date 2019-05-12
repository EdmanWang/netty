package chatService;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

// 聊天服务器
public class ChatService {

    public static void main(String[] args) throws Exception {

        // 主线程组
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        // 从线程组
        EventLoopGroup childGroup = new NioEventLoopGroup();

        // 启动类
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(parentGroup, childGroup).channel(NioServerSocketChannel.class).childHandler(new ChildHandler());
        Channel channel = serverBootstrap.bind(8888).sync().channel();
       try {
           channel.closeFuture().sync();
       }finally {
           parentGroup.shutdownGracefully().sync();
           childGroup.shutdownGracefully().sync();
       }

    }
}
