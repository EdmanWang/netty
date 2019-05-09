package helloNetty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

// netty 的启动类
public class HelloNettyServer {

    public static void main(String[] args) throws Exception {
        // 主线程组
        NioEventLoopGroup parentGroup = new NioEventLoopGroup();
        // 从线程组
        NioEventLoopGroup childGroup = new NioEventLoopGroup();

        // 启动类
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 为启动类添加主线程组，和从线程组。并为其天的nio类型的通道（双向通道）。并在通道中添加管道中的工具类
        serverBootstrap.group(parentGroup, childGroup).channel(NioServerSocketChannel.class).childHandler(new ChildHandle());

        // 为启动类的通道以同步的方式绑定一个端口
        Channel channel = serverBootstrap.bind(8088).sync().channel();


        try {
            // 关闭启动类通道
            channel.closeFuture().sync();
        } finally {
            parentGroup.shutdownGracefully().sync();
            childGroup.shutdownGracefully().sync();
        }
    }
}
