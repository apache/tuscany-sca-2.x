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

import java.net.UnknownHostException;

import junit.framework.Assert;

import org.junit.Test;

public class ConfigURITestCase {

    @Test
    public void testInvalidPrefix() throws UnknownHostException {
        try {
            new ConfigURI("foo");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testDomainName() throws UnknownHostException {
        ConfigURI configURI = new ConfigURI("tuscany:myDomain");
        Assert.assertEquals("myDomain", configURI.getDomainName());
        Assert.assertFalse(configURI.isMulticastDisabled());
    }

    @Test
    public void testListenAddr() throws UnknownHostException {
        ConfigURI configURI = new ConfigURI("tuscany:myDomain?listen=4321");
        Assert.assertEquals("myDomain", configURI.getDomainName());
        Assert.assertFalse(configURI.isMulticastDisabled());
        Assert.assertEquals(4321, configURI.getListenPort());
        Assert.assertNull(configURI.getBindAddress());
    }
    @Test
    public void testListenAddr2() throws UnknownHostException {
        ConfigURI configURI = new ConfigURI("tuscany:myDomain?listen=1.1.1.1:4321");
        Assert.assertEquals("myDomain", configURI.getDomainName());
        Assert.assertFalse(configURI.isMulticastDisabled());
        Assert.assertEquals(4321, configURI.getListenPort());
        Assert.assertEquals("1.1.1.1", configURI.getBindAddress());
    }

    @Test
    public void testMulticase1() throws UnknownHostException {
        ConfigURI configURI = new ConfigURI("tuscany:myDomain?multicast=off");
        Assert.assertEquals("myDomain", configURI.getDomainName());
        Assert.assertTrue(configURI.isMulticastDisabled());
    }

    @Test
    public void testMulticase2() throws UnknownHostException {
        ConfigURI configURI = new ConfigURI("tuscany:myDomain?multicast=1.2.3.4:67");
        Assert.assertEquals("myDomain", configURI.getDomainName());
        Assert.assertFalse(configURI.isMulticastDisabled());
        Assert.assertEquals("1.2.3.4", configURI.getMulticastAddress());
        Assert.assertEquals(67, configURI.getMulticastPort());
    }

    @Test
    public void testMulticase3() throws UnknownHostException {
        ConfigURI configURI = new ConfigURI("tuscany:myDomain?multicast=1.2.3.4");
        Assert.assertEquals("myDomain", configURI.getDomainName());
        Assert.assertFalse(configURI.isMulticastDisabled());
        Assert.assertEquals("1.2.3.4", configURI.getMulticastAddress());
        Assert.assertEquals(51482, configURI.getMulticastPort());
    }

    @Test
    public void testPassword() {
        ConfigURI configURI = new ConfigURI("tuscany:myDomain?password=bla");
        Assert.assertEquals("myDomain", configURI.getDomainName());
        Assert.assertEquals("bla", configURI.getPassword());
    }

    @Test
    public void testRemotes() throws UnknownHostException {
        ConfigURI configURI = new ConfigURI("tuscany:myDomain?remotes=1.1.1.1:23,2.2.2.2");
        Assert.assertEquals("myDomain", configURI.getDomainName());
        Assert.assertTrue(configURI.isMulticastDisabled());
        Assert.assertEquals(2, configURI.getRemotes().size());
        Assert.assertEquals("1.1.1.1:23", configURI.getRemotes().get(0));
        Assert.assertEquals("2.2.2.2:14820", configURI.getRemotes().get(1));
    }

}
