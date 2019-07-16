package httpService.connector.nettyConnector;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

public class HttpPiplineInitializer extends ChannelInitializer<Channel> {
    private final boolean sslEnable;
    private final SslContext sslContext;

    public HttpPiplineInitializer() {
        this.sslEnable = false;
        this.sslContext = null;
    }

    public HttpPiplineInitializer(SslContext sslContext) {
        this.sslEnable = true;
        this.sslContext = sslContext;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslEnable && sslContext != null) {
            SSLEngine engine = sslContext.newEngine(ch.alloc());
            pipeline.addFirst(new SslHandler(engine));
        }
        pipeline.addLast(new HttpClientCodec())
                .addLast(new HttpObjectAggregator(512 * 1024));
    }
}
