package de.codecentric.etcd;

import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.assertj.core.api.StrictAssertions.fail;

public class Etcd4JSyncTest {

    private EtcdClient client;
    private String key;
    private String value;

    @Before
    public void setUp() throws Exception {
        client = new EtcdClient(URI.create("http://192.168.59.103:7001"));
        key = String.valueOf(Math.abs(new Random().nextInt()));
        value = UUID.randomUUID().toString();
    }

    @Test
    public void testGetVersion() throws Exception {
        assertThat(client.getVersion()).isEqualTo("etcd 2.0.12");
    }

    @Test
    public void testPutGetKey() throws Exception {
        assertThat(client.put("forever" + key, value).send().get().node.value).isEqualTo(value);
        assertThat(client.get("forever" + key).send().get().node.value).isEqualTo(value);
    }

    @Test
    public void testPutGetTemporaryKey() throws Exception {
        assertThat(client.put("temporary" + key, value).ttl(1).send().get().node.value).isEqualTo(value);
        assertThat(client.get("temporary" + key).send().get().node.value).isEqualTo(value);
        Thread.sleep(1500);
        try {
            client.get("temporary" + key).send().get();
            fail("Get on key temporary" + key + " should fail");
        } catch (EtcdException e) {
            assertThat(e.errorCode).isEqualTo(100);
        }
    }

    @Test
    public void testPutGetDir() throws Exception {
        assertThat(client.putDir("dir" + key).send().get().node.dir).isTrue();
        assertThat(client.put("dir" + key + "/key", value).send().get().node.value).isEqualTo(value);
        EtcdKeysResponse.EtcdNode node = client.getDir("dir" + key).send().get().node;
        assertThat(node.dir).isTrue();
        assertThat(node.nodes.size()).isEqualTo(1);
        assertThat(node.nodes.get(0).value).isEqualTo(value);
    }

}
