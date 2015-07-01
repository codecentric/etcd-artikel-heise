package de.codecentric.etcd;

import jetcd.EtcdClient;
import jetcd.EtcdClientFactory;
import jetcd.EtcdException;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class JEtcdTest {

  private EtcdClient client;
  private String key;
  private String value;

  @Before
  public void setUp() throws Exception {
    client = EtcdClientFactory.newInstance("http://192.168.59.103:7001");
    key = String.valueOf(Math.abs(new Random().nextInt()));
    value = UUID.randomUUID().toString();
  }

  @Test
  public void testGetVersion() throws Exception {
    assertEquals("etcd 2.0.12", client.version());
  }

  @Test
  public void testPutGetKey() throws Exception {
    client.set("forever" + key, value);
    assertEquals(value, client.get("forever" + key));
  }

  @Test
  public void testPutGetTemporaryKey() throws Exception {
    client.set("temporary" + key, value, 1);
    assertEquals(value, client.get("temporary" + key));
    Thread.sleep(1500);
    try {
      client.get("temporary" + key);
      fail("Get on key temporary" + key +  " should fail");
    } catch(EtcdException e){
      assertEquals(100, e.getErrorCode());
    }
  }
}
