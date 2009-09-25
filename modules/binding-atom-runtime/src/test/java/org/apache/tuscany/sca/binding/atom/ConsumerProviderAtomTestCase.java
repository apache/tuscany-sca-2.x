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

import java.util.Date;

import junit.framework.Assert;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.tuscany.sca.binding.atom.collection.Collection;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case for the given package.
 */
public class ConsumerProviderAtomTestCase {

	protected static SCADomain scaConsumerDomain;
	protected static SCADomain scaProviderDomain;
	protected static CustomerClient testService;
	protected static Abdera abdera;

	@BeforeClass
	public static void init() throws Exception {
		System.out.println(">>>AtomBindingIntegratedTestCase.init entry");
		scaProviderDomain = SCADomain.newInstance("org/apache/tuscany/sca/binding/atom/Provider.composite");
		scaConsumerDomain = SCADomain.newInstance("org/apache/tuscany/sca/binding/atom/Consumer.composite");
		testService = scaConsumerDomain.getService(CustomerClient.class,"CustomerClient");
		abdera = new Abdera();
	}

	@AfterClass
	public static void destroy() throws Exception {
		System.out.println(">>>AtomBindingIntegratedTestCase.destroy entry");
		scaConsumerDomain.close();
		scaProviderDomain.close();
	}

	@Test
	public void testPrelim() throws Exception {
		Assert.assertNotNull(scaProviderDomain);
		Assert.assertNotNull(scaConsumerDomain);
		Assert.assertNotNull(testService);
		Assert.assertNotNull(abdera);
	}

	@Test
	public void testEntry() throws Exception {
		// System.out.println( getClass().getName() + ".testEntry entry" );
		Entry entry = abdera.newEntry();
		Assert.assertNotNull(entry);

		String testTitle = "Sponge Bob";
		entry.setTitle(testTitle);
		Assert.assertEquals(testTitle, entry.getTitle());

		String testContent = "This is the content";
		entry.setContent(testContent);
		Assert.assertEquals(testContent, entry.getContent());

		Date now = new Date();
		entry.setEdited(now);
		Assert.assertEquals(now, entry.getEdited());

		Date later = new Date();
		entry.setPublished(later);
		Assert.assertEquals(later, entry.getPublished());

		String testSummary = "This is the summary";
		entry.setSummary(testSummary);
		Assert.assertEquals(testSummary, entry.getSummary());
	}

	@Test
	public void testCustomerCollection() throws Exception {
		System.out.println(getClass().getName()	+ ".testCustomerCollection entry");
		Collection resourceCollection = testService.getCustomerCollection();
		Assert.assertNotNull(resourceCollection);
		
		Entry newEntry = newEntry("Sponge Bob");
	        System.out.println(">>> post entry=" + newEntry.getTitle());
	        newEntry = resourceCollection.post(newEntry);
	        System.out.println("<<< post id=" + newEntry.getId() + " entry=" + newEntry.getTitle());

	        newEntry = newEntry("Jane Bond");
	        System.out.println(">>> post entry=" + newEntry.getTitle());
	        newEntry = resourceCollection.post(newEntry);
	        System.out.println("<<< post id=" + newEntry.getId() + " entry=" + newEntry.getTitle());

	        System.out.println(">>> get id=" + newEntry.getId());
	        Entry entry = resourceCollection.get(newEntry.getId().toString());
	        System.out.println("<<< get id=" + entry.getId() + " entry=" + entry.getTitle());

	        System.out.println(">>> put id=" + newEntry.getId() + " entry=" + entry.getTitle());
	        resourceCollection.put(entry.getId().toString(), updateEntry(entry, "James Bond"));
	        System.out.println("<<< put id=" + entry.getId() + " entry=" + entry.getTitle());

	        System.out.println(">>> delete id=" + entry.getId());
	        resourceCollection.delete(entry.getId().toString());
	        System.out.println("<<< delete id=" + entry.getId());
	        
	        System.out.println(">>> get collection");
	        Feed feed = resourceCollection.getFeed();
	        System.out.println("<<< get collection");
	        for (Object o : feed.getEntries()) {
	            Entry e = (Entry)o;
	            System.out.println("id = " + e.getId() + " entry = " + e.getTitle());
	        }
	}

	private Entry newEntry(String value) {
		Entry entry = abdera.newEntry();
		entry.setTitle("customer " + value);

		Content content = abdera.getFactory().newContent();
		content.setContentType(Content.Type.TEXT);
		content.setValue(value);
		entry.setContentElement(content);

		return entry;
	}

	private Entry updateEntry(Entry entry, String value) {
		entry.setTitle("customer " + value);

		Content content = abdera.getFactory().newContent();
		content.setContentType(Content.Type.TEXT);
		content.setValue(value);
		entry.setContentElement(content);

		return entry;
	}
}