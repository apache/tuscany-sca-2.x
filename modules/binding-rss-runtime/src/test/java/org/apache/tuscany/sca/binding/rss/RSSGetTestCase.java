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

package org.apache.tuscany.sca.binding.rss;

import junit.framework.Assert;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Basic test case that will get the feed entries from an RSS feed.
 */
public class RSSGetTestCase {
    protected static SCADomain scaConsumerDomain;
    protected static SCADomain scaProviderDomain;
    protected static CustomerClient testService;

    @BeforeClass
    public static void init() throws Exception {
        System.out.println(">>>RSSGetTestCase.init entry");
        scaProviderDomain = SCADomain.newInstance("org/apache/tuscany/sca/binding/rss/Provider.composite");
        scaConsumerDomain = SCADomain.newInstance("org/apache/tuscany/sca/binding/rss/Consumer.composite");
        testService = scaConsumerDomain.getService(CustomerClient.class, "CustomerClient");
    }

    @AfterClass
    public static void destroy() throws Exception {
        // System.out.println(">>>RSSGetTestCase.destroy entry");
        if (scaConsumerDomain != null) {
            scaConsumerDomain.close();
        }
        if (scaProviderDomain != null) {
            scaProviderDomain.close();
        }
    }

    @Test
    public void testPrelim() throws Exception {
        Assert.assertNotNull(scaProviderDomain);
        Assert.assertNotNull(scaConsumerDomain);
        Assert.assertNotNull(testService);
    }

    @Test
    public void testRSSGet() throws Exception {
        testService.testCustomerCollection();
    }
}
