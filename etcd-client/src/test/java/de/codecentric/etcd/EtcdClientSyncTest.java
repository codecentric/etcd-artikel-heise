package de.codecentric.etcd;

import org.boon.etcd.ClientBuilder;
import org.boon.etcd.Etcd;
import org.boon.etcd.Node;
import org.boon.etcd.Response;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class EtcdClientSyncTest {
    private Etcd client;
    private String key;
    private String value;

    @Before
    public void setUp() throws Exception {
        client = createClient();
        key = String.valueOf(Math.abs(new Random().nextInt()));
        value = UUID.randomUUID().toString();
    }

    private Etcd createClient() {
        return ClientBuilder.builder()
                .hosts(URI.create("http://192.168.59.103:7001"))
                .createClient();
    }

    @Test
    public void testPutGetKey() throws Exception {
        assertThat(client.set("forever" + key, value).node().getValue()).isEqualTo(value);
        assertThat(client.get("forever" + key).node().getValue()).isEqualTo(value);
    }

    @Test
    public void testPutGetTemporaryKey() throws Exception {
        assertThat(client.setTemp("temporary" + key, value, 1).node().getValue()).isEqualTo(value);
        assertThat(client.get("temporary" + key).node().getValue()).isEqualTo(value);
        Thread.sleep(1500);
        Response response = client.get("temporary" + key);
        assertThat(response.wasError()).isTrue();
        assertThat(response.responseCode()).isEqualTo(404);
    }

    @Test
    public void testPutGetDir() throws Exception {
        assertThat(client.createDir("dir" + key).node().isDir()).isTrue();
        assertThat(client.set("dir" + key + "/key", value).node().getValue()).isEqualTo(value);
        Node node = client.get("dir" + key).node();
        assertThat(node.isDir()).isTrue();
        assertThat(node.getNodes()).hasSize(1);
        assertThat(node.getNodes().get(0).getValue()).isEqualTo(value);
    }

    @Test
    public void testWaitForChange() throws Exception{
        CountDownLatch cl = new CountDownLatch(1);
        String compKey = "forever" + key;
        assertThat(client.set(compKey, value).node().getValue()).isEqualTo(value);
        client.wait(response -> cl.countDown(), compKey);
        Thread.sleep(1000);
        createClient().set(compKey, value + 1);
        assertThat(cl.await(2000, TimeUnit.MILLISECONDS)).as("Timed out waiting for update").isTrue();
    }
}
