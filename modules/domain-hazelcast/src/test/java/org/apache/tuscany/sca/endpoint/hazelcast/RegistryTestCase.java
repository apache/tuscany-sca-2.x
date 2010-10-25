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

package org.apache.tuscany.sca.endpoint.hazelcast;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import com.hazelcast.config.Config;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.nio.Address;

@Ignore
public class RegistryTestCase {

    @Test
    public void test1() throws UnknownHostException {

        HazelcastInstance h1 = create("54327", 9001);

        IMap<Object, Object> h1map = h1.getMap("mymap");
        h1map.put("key1", "bla1");
        Assert.assertEquals("bla1", h1map.get("key1"));

        HazelcastInstance h2 = create("false", 9002, 9001);
        IMap<Object, Object> h2map = h2.getMap("mymap");
        Assert.assertEquals("bla1", h2map.get("key1"));

        HazelcastInstance h3 = create("false", 9003, 9002);
        IMap<Object, Object> h3map = h3.getMap("mymap");
        Assert.assertEquals("bla1", h3map.get("key1"));

        h3map.put("k3", "v3");
        h2map.put("k2", "v2");
        
        Assert.assertEquals("v2", h1map.get("k2"));
        Assert.assertEquals("v3", h1map.get("k3"));
        Assert.assertEquals("v2", h2map.get("k2"));
        Assert.assertEquals("v3", h2map.get("k3"));
        Assert.assertEquals("v2", h3map.get("k2"));
        Assert.assertEquals("v3", h3map.get("k3"));
        
        HazelcastInstance h4 = create("54328", 9004, 9001);
        IMap<Object, Object> h4map = h4.getMap("mymap");
//        Assert.assertNull(h4map.get("k2"));
//        Assert.assertNull(h4map.get("k3"));
        Assert.assertEquals("v2", h4map.get("k2"));
        Assert.assertEquals("v3", h4map.get("k3"));

//        HazelcastInstance h5 = create("false", 9005, 9003, 9004);
        HazelcastInstance h5 = create("54328", 9005);

//        Assert.assertEquals("v2", h4map.get("k2"));
//        Assert.assertEquals("v3", h4map.get("k3"));
        
        IMap<Object, Object> h5map = h5.getMap("mymap");
        Assert.assertEquals("v2", h5map.get("k2"));
        Assert.assertEquals("v3", h5map.get("k3"));
        
        h1.shutdown();
        
        Assert.assertEquals("v2", h2map.get("k2"));
        Assert.assertEquals("v3", h2map.get("k3"));
        Assert.assertEquals("v2", h3map.get("k2"));
        Assert.assertEquals("v3", h3map.get("k3"));
        Assert.assertEquals("v2", h4map.get("k2"));
        Assert.assertEquals("v3", h4map.get("k3"));

        h3map.put("key1a", "bla1a");
        
        Assert.assertEquals("bla1a", h2map.get("key1a"));
        Assert.assertEquals("bla1a", h3map.get("key1a"));
        Assert.assertEquals("bla1a", h4map.get("key1a"));
        
//        HazelcastInstance h4 = create(true, 9004, 9003);
//        HazelcastInstance h5 = create(true, 9005);
//        IMap<Object, Object> h5map = h5.getMap("mymap");
//        Assert.assertEquals("bla1", h5map.get("key1"));

//        HazelcastInstance h6 = create(false, 9006, 9005);
//        IMap<Object, Object> h6map = h6.getMap("mymap");
//        Assert.assertEquals("bla1", h6map.get("key1"));

    }

    private HazelcastInstance create(String multicast, int listenPort, int... connectPorts) throws UnknownHostException {
        Config config = new XmlConfigBuilder().build();
        config.setPort(listenPort);
        config.setPortAutoIncrement(false);

        // declare the interface Hazelcast should bind to
        config.getNetworkConfig().getInterfaces().clear();
        config.getNetworkConfig().getInterfaces().addInterface(InetAddress.getLocalHost().getHostAddress());
        config.getNetworkConfig().getInterfaces().setEnabled(true);

        if ("false".equals(multicast)) {
            config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        } else {
            config.getNetworkConfig().getJoin().getMulticastConfig().setMulticastPort(Integer.parseInt(multicast));
        }
        
        if (connectPorts.length > 0) {
            TcpIpConfig tcpconfig = config.getNetworkConfig().getJoin().getTcpIpConfig();
            tcpconfig.setEnabled(true);

            List<Address> lsMembers = tcpconfig.getAddresses();
            lsMembers.clear();
            for (int p : connectPorts) {
                lsMembers.add(new Address(InetAddress.getLocalHost(), p));
            }
        }

        return Hazelcast.newHazelcastInstance(config);
    }

}
