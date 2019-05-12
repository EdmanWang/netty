package chatService;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

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

        // 自定义拦截器（助手类）
        pipeline.addLast("CustomerChatHandler", new CustomerChatHandler());
    }
}
