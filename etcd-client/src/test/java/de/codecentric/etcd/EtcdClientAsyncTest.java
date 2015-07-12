package de.codecentric.etcd;

import org.boon.etcd.ClientBuilder;
import org.boon.etcd.Etcd;
import org.boon.etcd.Response;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class EtcdClientAsyncTest {
    private AsyncTestContext<Response> ctx;
    private Etcd client;
    private String key;
    private String value;

    @Before
    public void setUp() throws Exception {
        client = ClientBuilder.builder()
                .hosts(URI.create("http://192.168.59.103:7001"))
                .createClient();
        key = String.valueOf(Math.abs(new Random().nextInt()));
        value = UUID.randomUUID().toString();
        ctx = new AsyncTestContext<>();
    }

    @Test
    public void testPutGetKey() throws Exception {
        client.set(ctx, "forever" + key, value);
        ctx.await();
        assertThat(ctx.response().node().getValue()).isEqualTo(value);
        ctx.reset();
        client.get(ctx, "forever" + key);
        ctx.await();
        assertThat(ctx.response().node().getValue()).isEqualTo(value);
    }

    @Test
    public void testPutGetTemporaryKey() throws Exception {
        client.setTemp(ctx, "temporary" + key, value, 1);
        ctx.await();
        assertThat(ctx.response().node().getValue()).isEqualTo(value);
        ctx.reset();
        client.get(ctx, "temporary" + key);
        ctx.await();
        assertThat(ctx.response().node().getValue()).isEqualTo(value);
        ctx.reset();
        Thread.sleep(2000);
        client.get(ctx, "temporary" + key);
        ctx.await();
        assertThat(ctx.response().responseCode()).isEqualTo(404);
    }

    @Test
    public void testPutGetDir() throws Exception {
        client.createDir(ctx, "dir" + key);
        ctx.await();
        assertThat(ctx.response().node().isDir()).isTrue();
        ctx.reset();
        client.set(ctx, "dir" + key + "/key", value);
        ctx.await();
        assertThat(ctx.response().node().getValue()).isEqualTo(value);
        ctx.reset();
        client.get(ctx, "dir" + key);
        ctx.await();
        assertThat(ctx.response().node().isDir()).isTrue();
        assertThat(ctx.response().node().getNodes()).hasSize(1);
        assertThat(ctx.response().node().getNodes().get(0).getValue()).isEqualTo(value);
    }
}
