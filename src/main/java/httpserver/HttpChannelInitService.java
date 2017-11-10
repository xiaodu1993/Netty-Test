package httpserver;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * @author maobing.dmb
 * @date 2017/11/09
 */
public class HttpChannelInitService extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new HttpResponseEncoder());
        ch.pipeline().addLast(new HttpRequestDecoder());
        ch.pipeline().addLast(new HttpChannelHander());
    }
}
