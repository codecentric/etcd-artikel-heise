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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EtcdClientSyncTest {
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
  }

  @Test
  public void testPutGetKey() throws Exception {
    assertEquals(value, client.set("forever" + key, value).node().getValue());
    assertEquals(value, client.get("forever" + key).node().getValue());
  }

  @Test
  public void testPutGetTemporaryKey() throws Exception {
    assertEquals(value, client.setTemp("temporary" + key, value, 1).node().getValue());
    assertEquals(value, client.get("temporary" + key).node().getValue());
    Thread.sleep(1500);
    Response response = client.get("temporary" + key);
    assertTrue(response.wasError());
    assertEquals(404, response.responseCode());
  }

  @Test
  public void testPutGetDir() throws Exception {
    assertTrue(client.createDir("dir" + key).node().isDir());
    assertEquals(value, client.set("dir" + key + "/key", value).node().getValue());
    Node node = client.get("dir" + key).node();
    assertTrue(node.isDir());
    assertEquals(1, node.getNodes().size());
    assertEquals(value, node.getNodes().get(0).getValue());
  }
}
