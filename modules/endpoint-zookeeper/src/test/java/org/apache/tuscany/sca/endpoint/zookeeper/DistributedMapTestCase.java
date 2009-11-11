/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.sca.endpoint.zookeeper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.server.PurgeTxnLog;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 */
public class DistributedMapTestCase implements Watcher {

    private static LocalZooKeeperServer server;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        server = new LocalZooKeeperServer();
    }

    @Test
    public void testMap() throws Exception {
        String[] args = new String[] {"8085", "target/zookeeper"};
        server.folk(args);
        ZooKeeper client = new ZooKeeper("localhost:8085", 500, this);
        synchronized (this) {
            wait(10000);
        }
        try {
            AbstractDistributedMap<String> map =
                new AbstractDistributedMap<String>(client, "testMap", getClass().getClassLoader());
            map.start();
            map.clear();
            String value = map.put("1", "A");
            Assert.assertNull(value);
            value = map.put("2", "B");
            Assert.assertNull(value);
            value = map.put("2", "C");
            Assert.assertEquals("B", value);
            value = map.get("2");
            Assert.assertEquals("C", value);
            value = map.remove("2");
            Assert.assertEquals("C", value);
            map.put("3", "D");
            Map<String, String> map1 = new HashMap<String, String>();
            map1.put("4", "E");
            map1.put("5", "F");
            map.putAll(map1);
            System.out.println(map);
            Assert.assertTrue(map.containsKey("5"));
            Assert.assertTrue(map.containsValue("F"));
            map.clear();
            Assert.assertEquals(0, map.size());
        } finally {
            client.close();
        }
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (server != null) {
            server.shutdown();
            File dir = new File("target/zookeeper");
            PurgeTxnLog.purge(dir, dir, 3);
        }
    }

    public void process(WatchedEvent event) {
        System.out.println(event);
        if (event.getPath() == null && event.getState() == KeeperState.SyncConnected) {
            synchronized (this) {
                notifyAll();
            }
        }
    }

}
