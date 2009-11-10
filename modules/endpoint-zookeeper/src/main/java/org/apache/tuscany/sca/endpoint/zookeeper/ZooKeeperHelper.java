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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.ZooKeeperServer.DataTreeBuilder;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;

/**
 * 
 */
public class ZooKeeperHelper implements Watcher {
    public ZooKeeper connect(String host, int port, int timeout) throws IOException {
        return new ZooKeeper(host + ":" + port, timeout, this);
    }

    public String getName(String uri) {
        String name = uri.replace("$", "$$");
        return name.replace('/', '$');
    }

    private byte[] getBytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String join(ZooKeeper zooKeeper, String domainURI, String nodeURI) throws KeeperException,
        InterruptedException {
        String domain = "/" + getName(domainURI);
        Stat stat = zooKeeper.exists(domain, false);
        String path = domain;
        if (stat == null) {
            path = zooKeeper.create(domain, getBytes(domainURI), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        path = path + "/" + getName(nodeURI);
        stat = zooKeeper.exists(path, false);
        if (stat != null) {
            return zooKeeper.create(path, getBytes(nodeURI), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } else {
            return path;
        }
    }

    public String getPath(String... uris) {
        StringBuffer buffer = new StringBuffer();
        for (String uri : uris) {
            buffer.append('/').append(getName(uri));
        }
        return buffer.toString();
    }

    public void put(ZooKeeper zooKeeper, String domainURI, String nodeURI, String endpointURI, byte[] endpoint)
        throws KeeperException, InterruptedException {
        String path = join(zooKeeper, domainURI, nodeURI);
        path = path + "/" + getName(endpointURI);
        Stat stat = zooKeeper.exists(path, false);
        if (stat == null) {
            zooKeeper.create(path, endpoint, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } else {
            zooKeeper.setData(path, endpoint, -1);
        }
    }

    public void remove(ZooKeeper zooKeeper, String domainURI, String nodeURI, String endpointURI)
        throws KeeperException, InterruptedException {
        String path = getPath(domainURI, nodeURI, endpointURI);
        zooKeeper.delete(path, -1);
    }

    public void remove(ZooKeeper zooKeeper, String domainURI, String nodeURI) throws InterruptedException,
        KeeperException {
        String node = getPath(domainURI, nodeURI);
        zooKeeper.delete(node, -1);
    }

    public byte[] get(ZooKeeper zooKeeper, String domainURI, String nodeURI, String endpointURI)
        throws KeeperException, InterruptedException {
        String path = getPath(domainURI, nodeURI, endpointURI);
        return zooKeeper.getData(path, false, new Stat());
    }

    public List<byte[]> get(ZooKeeper zooKeeper, String domainURI, String nodeURI) throws KeeperException,
        InterruptedException {
        String node = getPath(domainURI, nodeURI);
        List<String> endpoints = zooKeeper.getChildren(node, false);
        List<byte[]> data = new ArrayList<byte[]>();
        for (String endpoint : endpoints) {
            Stat stat = new Stat();
            data.add(zooKeeper.getData(endpoint, false, stat));
        }
        return data;
    }

    public List<byte[]> get(ZooKeeper zooKeeper, String domainURI) throws KeeperException, InterruptedException {
        String path = getPath(domainURI);
        List<String> nodes = zooKeeper.getChildren(path, false);
        List<byte[]> data = new ArrayList<byte[]>();
        for (String node : nodes) {
            List<String> endpoints = zooKeeper.getChildren(node, false);
            for (String endpoint : endpoints) {
                Stat stat = new Stat();
                data.add(zooKeeper.getData(endpoint, false, stat));
            }
        }
        return data;
    }

    public ZooKeeper create(String connectString, int timeout, long sessionId, byte[] password) throws IOException {
        return new ZooKeeper(connectString, timeout, this, sessionId, password);
    }

    public void process(WatchedEvent event) {
        System.out.println(event);
    }

    public ZooKeeperServer createServer(File dataDir, File snapDir, int tickTime) throws IOException {
        FileTxnSnapLog log = new FileTxnSnapLog(dataDir, snapDir);
        DataTreeBuilder builder = new ZooKeeperServer.BasicDataTreeBuilder();
        return new ZooKeeperServer(log, tickTime, builder);
    }

    public void put(ZooKeeper zooKeeper, String key, Object value) throws KeeperException, InterruptedException {
        byte[] data = serialize(value);
        List<ACL> acls = Collections.emptyList();
        zooKeeper.create(key, data, acls, CreateMode.PERSISTENT);
    }

    public byte[] serialize(Object value) {
        return null;
    }
    
    private Map<String, String> parseURI(Map<String, String> attributes, String domainRegistryURI) {
        Map<String, String> map = new HashMap<String, String>();
        if (attributes != null) {
            map.putAll(attributes);
        }
        URI uri = URI.create(domainRegistryURI);
        if (uri.getHost() != null) {
            map.put("host", uri.getHost());
        }
        if (uri.getPort() != -1) {
            map.put("port", String.valueOf(uri.getPort()));
        }
        int index = domainRegistryURI.indexOf('?');
        if (index == -1) {
            return map;
        }
        String query = domainRegistryURI.substring(index + 1);
        try {
            query = URLDecoder.decode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
        String[] params = query.split("&");
        for (String param : params) {
            index = param.indexOf('=');
            if (index != -1) {
                map.put(param.substring(0, index), param.substring(index + 1));
            }
        }
        return map;
    }    

    public static void main(final String[] args) throws Exception {
        final String options[] =
            args.length != 0 ? args
                : new String[] {"9999", System.getProperty("java.io.tmpdir") + File.separator + "zookeeper"};
        Thread thread = new Thread() {
            public void run() {
                ZooKeeperServerMain.main(options);
            }
        };
        thread.start();
        Thread.sleep(1000);
        ZooKeeper zooKeeper = new ZooKeeperHelper().connect("localhost", 9999, 500);
        System.out.println(zooKeeper.getSessionId());
        try {
            String data = new String(zooKeeper.getData("/x", false, null));
            System.out.println(data);
        } catch (KeeperException e) {
            if (e.code() == Code.NONODE) {
                zooKeeper.create("/x", "X".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        }
        zooKeeper.create("/x/y", "XY".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        Thread.sleep(1000);
        String data = new String(zooKeeper.getData("/x/y", true, null));
        System.out.println(data);
        zooKeeper.close();
        Thread.sleep(500);
        System.exit(0);
    }
}
