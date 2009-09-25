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
package org.apache.tuscany.sca.binding.atom;

import java.util.UUID;

import junit.framework.Assert;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.tuscany.sca.binding.atom.collection.Collection;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AtomDeleteTestCase {
    protected static SCADomain scaConsumerDomain;
    protected static SCADomain scaProviderDomain;
    protected static CustomerClient testService;
    protected static Abdera abdera;

    @BeforeClass
    public static void init() throws Exception {
        System.out.println(">>>AtomDeleteTestCase.init entry");
        scaProviderDomain = SCADomain.newInstance("org/apache/tuscany/sca/binding/atom/Provider.composite");
        scaConsumerDomain = SCADomain.newInstance("org/apache/tuscany/sca/binding/atom/Consumer.composite");
        testService = scaConsumerDomain.getService(CustomerClient.class, "CustomerClient");
        abdera = new Abdera();
    }

    @AfterClass
    public static void destroy() throws Exception {
        // System.out.println(">>>AtomDeleteTestCase.destroy entry");
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
        Assert.assertNotNull(abdera);
    }

    @Test
    public void testAtomDelete() throws Exception {
        Collection resourceCollection = testService.getCustomerCollection();
        Assert.assertNotNull(resourceCollection);

        Entry postEntry = postEntry("Sponge Bob");
        System.out.println(">>> post entry= " + postEntry.getTitle());

        Entry newEntry = resourceCollection.post(postEntry);
        System.out.println("<<< Entry posted for " + newEntry.getTitle());

        System.out.println(">>> get id=" + newEntry.getId());

        resourceCollection.delete(newEntry.getId().toString());

    }

    @Test
    public void testAtomDeleteException() throws Exception {
        Collection resourceCollection = testService.getCustomerCollection();
        Assert.assertNotNull(resourceCollection);

        try {
            // Generates custom ID
            String id = "urn:uuid:customer-" + UUID.randomUUID().toString();
            resourceCollection.delete(id);
        } catch (Exception e) {
            // ID doesn't match with the existing IDs and NotFoundException is
            // thrown
            Assert.assertEquals("NotFoundException", e.getClass().getSimpleName());
        }

    }

    private Entry postEntry(String value) {
        Entry entry = abdera.newEntry();
        entry.setTitle("customer " + value);

        Content content = abdera.getFactory().newContent();
        content.setContentType(Content.Type.TEXT);
        content.setValue(value);
        entry.setContentElement(content);

        return entry;
    }

}
