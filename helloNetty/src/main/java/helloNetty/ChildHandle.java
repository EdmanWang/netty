package helloNetty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

// 这就是通道
public class ChildHandle extends ChannelInitializer {

    protected void initChannel(Channel channel) throws Exception {
        // 通过通道得到管道
        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast("HttpServerCodec", new HttpServerCodec());
        pipeline.addLast("CustomerHandler", new CustomerHandler());
        // 大数据流
        pipeline.addLast("ChunkedWriteHandler", new ChunkedWriteHandler());
        // 对httpMessage 进行聚合，集合成fullhttphandler 或者fullhttpreponse;
        pipeline.addLast("HttpObjectAggregator", new HttpObjectAggregator(1024 * 64));

        //浏览器访问服务器的一个路由
        pipeline.addLast("WebSocketServerProtocolHandler", new WebSocketServerProtocolHandler("/ws"));

    }
}
