package httpService.connector.nettyConnector;

import com.alibaba.fastjson.JSON;
import httpService.HttpMethod;
import httpService.connector.RemoteConnector;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class NettyConnector implements RemoteConnector {
    @Override
    public String get(
            String url,
            HttpMethod method,
            Map<String, String> param,
            Map<String, String> headers,
            Object entity) {

        String host = "";
        int port = 0;
        Channel channel = Client.getChannel(host, port);
        String stringEntity = JSON.toJSONString(entity);
        ByteBuf byteBuf = channel.alloc().buffer(stringEntity.length());
        byteBuf.writeCharSequence(stringEntity, CharsetUtil.UTF_8);
        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                io.netty.handler.codec.http.HttpMethod.valueOf(method.name()),
                url,
                byteBuf
        );
        FullHttpResponse response;

        return null;
    }

    @Override
    public CompletableFuture<String> getAsync(
            String url,
            HttpMethod method,
            Map<String, String> param,
            Map<String, String> headers,
            Object entity,
            ExecutorService executor) {
        return null;
    }
}
