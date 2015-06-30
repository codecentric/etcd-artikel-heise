package de.codecentric.etcd;

import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Etcd4JTest {

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
    assertEquals("etcd 2.0.12", client.getVersion());
  }

  @Test
  public void testPutGetKey() throws Exception {
    assertEquals(value, client.put("forever" + key, value).send().get().node.value);
    assertEquals(value, client.get("forever" + key).send().get().node.value);
  }

  @Test
  public void testPutGetTemporaryKey() throws Exception {
    assertEquals(value, client.put("temporary" + key, value).ttl(2).send().get().node.value);
    assertEquals(value, client.get("temporary" + key).send().get().node.value);
    Thread.sleep(2000);
    try {
      client.get("temporary" + key).send().get();
    } catch(EtcdException e){
      assertEquals(100, e.errorCode);
    }
  }

  @Test
  public void testPutGetDir() throws Exception {
    assertTrue(client.putDir("dir" + key).send().get().node.dir);
    assertEquals(value, client.put("dir" + key + "/key", value).send().get().node.value);
    EtcdKeysResponse.EtcdNode node = client.getDir("dir" + key).send().get().node;
    assertTrue(value, node.dir);
    assertEquals(1, node.nodes.size());
    assertEquals(value, node.nodes.get(0).value);
  }
}
