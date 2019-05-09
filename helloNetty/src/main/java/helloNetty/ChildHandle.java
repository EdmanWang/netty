package helloNetty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpServerCodec;

// 这就是通道
public class ChildHandle extends ChannelInitializer{

    protected void initChannel(Channel channel) throws Exception {
        // 通过通道得到管道
        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast("HttpServerCodec", new HttpServerCodec());
        pipeline.addLast("CustomerHandler",new CustomerHandler());

    }
}
