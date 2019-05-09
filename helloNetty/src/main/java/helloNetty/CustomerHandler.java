package helloNetty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

// 自定义工具类（拦截器）
public class CustomerHandler extends SimpleChannelInboundHandler {
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        Channel channel = ctx.channel();

        // 打印客户端的远程地址
        System.out.println(channel.remoteAddress());
        ByteBuf content = Unpooled.copiedBuffer("hello wgx", CharsetUtil.UTF_8);

        // 需要构建一个httpResponse
        FullHttpResponse reponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

        // 为相应数据添加数据类型和长度
        reponse.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
        reponse.headers().set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());

        // 将数据相应到客户端
        ctx.writeAndFlush(reponse);
    }
}
