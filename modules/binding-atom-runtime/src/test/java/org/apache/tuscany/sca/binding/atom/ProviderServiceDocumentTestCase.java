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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.tuscany.sca.binding.atom.collection.Collection;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests use of service documents provided by atom binding based collections.
 * Uses the SCA provided Provider composite to act as a server.
 * Uses the Abdera provided Client to act as a client.
 */
public class ProviderServiceDocumentTestCase {
	public final static String providerURI = "http://localhost:8084/customer";
	protected static SCADomain scaProviderDomain;
    protected static SCADomain scaConsumerDomain;
    protected static CustomerClient testService;
    protected static Abdera abdera;
    protected static AbderaClient client;
    protected static Parser abderaParser;    

	@BeforeClass
	public static void init() throws Exception {
		scaProviderDomain = SCADomain.newInstance("org/apache/tuscany/sca/binding/atom/Provider.composite");
        scaConsumerDomain = SCADomain.newInstance("org/apache/tuscany/sca/binding/atom/Consumer.composite");
        testService = scaConsumerDomain.getService(CustomerClient.class, "CustomerClient");
		abdera = new Abdera();
		client = new AbderaClient(abdera);
		abderaParser = Abdera.getNewParser();
	}

    @AfterClass
    public static void destroy() throws Exception {
        if (scaProviderDomain != null) {
            scaProviderDomain.close();
        }
    }

	@Test
	public void testPrelim() throws Exception {
		Assert.assertNotNull(scaProviderDomain);
		Assert.assertNotNull( client );
	}
			
	@Test
    public void testFeedBasics() throws Exception {		
		// Normal feed request
		ClientResponse res = client.get(providerURI);
		Assert.assertNotNull(res);
		try {
			// Assert feed provided since no predicates
			Assert.assertEquals(200, res.getStatus());
			Assert.assertEquals(ResponseType.SUCCESS, res.getType());
	    	// AtomTestCaseUtils.printResponseHeaders( "Feed response headers:", "   ", res );
	    	// System.out.println("Feed response content:");
	    	// AtomTestCaseUtils.prettyPrint(abdera, res.getDocument());

	    	// Perform other tests on feed.
	    	// Warning. AbderaClient.getEntityTag is very particular on tag pattern.
			// Document<Feed> doc = res.getDocument();
	    	String body = read( res.getInputStream() );
			// RFC 4287 requires non-null id, title, updated elements
			Assert.assertTrue( -1 != body.indexOf( "</id>" ));
			Assert.assertTrue( -1 != body.indexOf( "</title>" ));
			Assert.assertTrue( -1 != body.indexOf( "</updated>" ));
		} finally {
			res.release();
		}
	}		

    @Test
    public void testServiceDocumentGet() throws Exception {
        Collection resourceCollection = testService.getCustomerCollection();
        Assert.assertNotNull(resourceCollection);

        Entry postEntry = postEntry("Sponge Bob");
        Entry newEntry = resourceCollection.post(postEntry);
        postEntry = postEntry("Austin Powers");
        newEntry = resourceCollection.post(postEntry);
        postEntry = postEntry("Count Dracula");
        newEntry = resourceCollection.post(postEntry);

		// Service document
		ClientResponse res = client.get(providerURI + "/atomsvc");
		Assert.assertNotNull(res);
		try {
			// Asser feed provided since no predicates
			Assert.assertEquals(200, res.getStatus());
			Assert.assertEquals(ResponseType.SUCCESS, res.getType());

	    	// Perform other tests on feed.
	    	// AtomTestCaseUtils.prettyPrint(abdera, res.getDocument());	    	
			Document<Service> serviceDoc = res.getDocument();
			Service service = serviceDoc.getRoot();
			Assert.assertNotNull( service );
			org.apache.abdera.model.Collection collection =  service.getCollection( "workspace", "customers" );
			String title = collection.getTitle();
			Assert.assertEquals("customers", title);
			String href = collection.getHref().toString();
			Assert.assertTrue( href.contains( "customer") );
		} finally {
			res.release();
		}
    }

	public static void printFeed( String title, String indent, Feed feed ) {
		if ( feed == null ) {
			System.out.println( title + " feed is null");
			return;
		}
			
		System.out.println( title );
		System.out.println( indent + "id=" + feed.getId() );
		System.out.println( indent + "title=" + feed.getTitle() );
		System.out.println( indent + "updated=" + feed.getUpdated() );
		System.out.println( indent + "author=" + feed.getAuthor() );
		// Collection collection = feed.getCollection();
		// if ( collection == null ) {
		// 	System.out.println( indent + "collection=null" );
		// } else {
		// 	System.out.println( indent + "collection=" + collection );
		// }
		// System.out.println( indent + "collection size=" + feed.getCollection() );
		// for (Collection collection : workspace.getCollections()) {
		//    if (collection.getTitle().equals("customers")) {
		//       String expected = uri + "customers";
		//       String actual = collection.getResolvedHref().toString();
		//       assertEquals(expected, actual);
		//    }
		// }

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

    /**
	 * Read response ream from the given socket.
	 * @param socket
	 * @return
	 * @throws IOException
	 */
	private static String read(InputStream inputStream) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader( inputStream ));
			StringBuffer sb = new StringBuffer();
			String str;
			while ((str = reader.readLine()) != null) {
				sb.append(str);
			}
			return sb.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
}
