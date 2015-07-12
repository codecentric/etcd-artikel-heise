package de.codecentric.etcd;

import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.responses.EtcdKeyAction;
import mousio.etcd4j.responses.EtcdKeysResponse;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class Etcd4JAsyncTest {
    private AsyncTestContext<EtcdKeysResponse> ctx;
    private EtcdClient client;
    private String key;
    private String value;

    @Before
    public void setUp() throws Exception {
        client = new EtcdClient(URI.create("http://192.168.59.103:7001"));
        key = String.valueOf(Math.abs(new Random().nextInt()));
        value = UUID.randomUUID().toString();
        ctx = new AsyncTestContext<>();
    }

    @Test
    public void testGetVersion() throws Exception {
        assertThat(client.getVersion()).isEqualTo("etcd 2.0.12");
    }

    @Test
    public void testAsyncPutAndDelete() throws Exception {
        client.put(key, value).send().addListener(ctx);
        ctx.await();
        assertThat(ctx.response().getException()).isNull();
        ctx.reset();
        client.delete(key).send().addListener(ctx);
        ctx.await(7000);
        assertThat(ctx.response().getException()).isNull();
        assertThat(ctx.response().get().action).isEqualTo(EtcdKeyAction.delete);
    }

    @Test
    public void testAsyncPutAndGet() throws Exception {
        client.put(key, value).send().addListener(ctx);
        ctx.await();
        assertThat(ctx.response().getException()).isNull();
        ctx.reset();
        client.delete(key).send().addListener(ctx);
        ctx.await(7000);
        assertThat(ctx.response().getException()).isNull();
        assertThat(ctx.response().get().action).isEqualTo(EtcdKeyAction.delete);
    }

    @Test
    public void testAsyncPut() throws Exception {
        client.put(key, value).send().addListener(ctx);
        ctx.await();
        assertThat(ctx.response().getException()).isNull();
        assertThat(ctx.response().get().action).isEqualTo(EtcdKeyAction.set);
    }

}
