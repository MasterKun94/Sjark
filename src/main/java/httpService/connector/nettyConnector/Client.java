package httpService.connector.nettyConnector;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Client {
    private static final Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    private static final EventLoopGroup group = new NioEventLoopGroup();
    private static final HttpPiplineInitializer initializer = new HttpPiplineInitializer();

    private static Channel start(String host, int port) {
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture future = bootstrap
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(initializer)
                .connect(host, port)
                .syncUninterruptibly();
        return future.channel();
    }

    private static String getKey(String host, int port) {
        return host + ":" + port;
    }

    public static Channel getChannel(String host, int port) {
        String key = getKey(host, port);
        Channel channel = channelMap.get(key);
        if (channel == null || !channel.isActive()) {
            channel = start(host, port);
        }
        channelMap.put(key, channel);
        return channel;
    }

}
