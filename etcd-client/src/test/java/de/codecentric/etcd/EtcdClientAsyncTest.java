package de.codecentric.etcd;

import org.boon.core.Handler;
import org.boon.etcd.ClientBuilder;
import org.boon.etcd.Etcd;
import org.boon.etcd.Response;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EtcdClientAsyncTest {
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
    client.set(new Handler<Response>() {
      public void handle(Response event) {
        assertEquals(value, event.node().getValue());
      }
    }, "forever" + key, value);

    Thread.sleep(1000);

    client.get(new Handler<Response>() {
      public void handle(Response event) {
        assertEquals(value, event.node().getValue());
      }
    }, "forever" + key);

    Thread.sleep(1000);
  }

  @Test
  public void testPutGetTemporaryKey() throws Exception {
    client.setTemp(new Handler<Response>() {
      public void handle(Response event) {
        assertEquals(value, event.node().getValue());
      }
    }, "temporary" + key, value, 1);

    Thread.sleep(1000);

    client.get(new Handler<Response>() {
      public void handle(Response event) {
        assertEquals(value, event.node().getValue());
      }
    }, "temporary" + key);

    Thread.sleep(1500);

    client.get(new Handler<Response>() {
      public void handle(Response event) {
        assertTrue(event.wasError());
        assertEquals(404, event.responseCode());
      }
    }, "temporary" + key);

    Thread.sleep(1000);
  }

  @Test
  public void testPutGetDir() throws Exception {
    client.createDir(new Handler<Response>() {
      public void handle(Response event) {
        assertTrue(event.node().isDir());
      }
    }, "dir" + key);

    Thread.sleep(1000);

    client.set(new Handler<Response>() {
      public void handle(Response event) {
        assertEquals(value, event.node().getValue());
      }
    }, "dir" + key + "/key", value);

    Thread.sleep(1000);

    client.get(new Handler<Response>() {
      public void handle(Response event) {
        assertTrue(event.node().isDir());
        assertEquals(1, event.node().getNodes().size());
        assertEquals(value, event.node().getNodes().get(0).getValue());
      }
    }, "dir" + key);

    Thread.sleep(1000);
  }
}
