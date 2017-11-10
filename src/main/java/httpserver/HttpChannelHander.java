package httpserver;

import java.util.Iterator;
import java.util.Set;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.ServerCookieEncoder;
import io.netty.util.CharsetUtil;

/**
 * @author maobing.dmb
 * @date 2017/11/09
 */
public class HttpChannelHander extends ChannelInboundHandlerAdapter {

    private HttpRequest request;
    private FullHttpResponse response;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            request = (HttpRequest)msg;
            String uri = request.getUri();
            String res;
            try {
                res = uri.substring(1);
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(res.getBytes("UTF-8")));
                setJsessionId(isHasJsessionId());
                setHeaders(response);
            } catch (Exception e) {
                res = "<html><body>Server Error</body></html>";
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(res.getBytes("UTF-8")));
            }
            if (response != null) {
                ctx.write(response);
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent)msg;
            ByteBuf buf = content.content();
            System.out.println(buf.toString(CharsetUtil.UTF_8));
            buf.release();
        }
    }

    private void setJsessionId(boolean isHasJsessionId) {
        if (!isHasJsessionId) {
            String str = ServerCookieEncoder.encode(HttpSession.SESSIONID, HttpSessionManager.getSessionId());
            response.headers().set(Names.SET_COOKIE, str);
        }
    }

    private boolean isHasJsessionId() {
        try {
            String cookieStr = request.headers().get("Cookie");
            Set<Cookie> cookies = CookieDecoder.decode(cookieStr);
            Iterator<Cookie> it = cookies.iterator();
            while (it.hasNext()) {
                Cookie cookie = it.next();
                if (cookie.getName().equals(HttpSession.SESSIONID)) {
                    System.out.println("JSESSIONID: " + cookie.getValue());
                }
                return cookie.getName().equals(HttpSession.SESSIONID) && HttpSessionManager.isHasJsessionId(
                    cookie.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setHeaders(FullHttpResponse response) {
        response.headers().set(Names.CONTENT_TYPE, "text/html");
        response.headers().set(Names.CONTENT_LANGUAGE,
            response.content().readableBytes());
        if (HttpHeaders.isKeepAlive(request)) {
            response.headers().set(Names.CONNECTION, Values.KEEP_ALIVE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server channelReadComplete...");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("server exception...");
        ctx.close();
    }
}
