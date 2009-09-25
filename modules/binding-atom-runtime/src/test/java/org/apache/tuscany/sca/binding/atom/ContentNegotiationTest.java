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

import java.io.IOException;
import java.io.Reader;
import java.util.Date;

import junit.framework.Assert;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Tests use of content negotiation for Atom binding in Tuscany.
 * Uses the SCA provided Provider composite to act as a server.
 * Uses the Abdera provided Client to act as a client.
 */
public class ContentNegotiationTest {
	public final static String providerURI = "http://localhost:8084/customer";
	protected static SCADomain scaProviderDomain;
	protected static CustomerClient testService;
    protected static Abdera abdera;
    protected static AbderaClient client;
    protected static Parser abderaParser;    
    protected static String lastId;

	@BeforeClass
	public static void init() throws Exception {
		System.out.println(">>>ContentNegotiationTest.init");
		scaProviderDomain = SCADomain.newInstance("org/apache/tuscany/sca/binding/atom/Provider.composite");
		abdera = new Abdera();
		client = new AbderaClient(abdera);
		abderaParser = Abdera.getNewParser();
	}

	@AfterClass
	public static void destroy() throws Exception {
		System.out.println(">>>ContentNegotiationTest.destroy");
		scaProviderDomain.close();
	}

	@Test
	public void testPrelim() throws Exception {
		Assert.assertNotNull(scaProviderDomain);
		Assert.assertNotNull( client );
	}
	
    @Test
	public void testPost() throws Exception {
		System.out.println(">>>ContentNegotiationTest.testPost");
		// Testing of entry creation
		Factory factory = abdera.getFactory();
		String customerName = "Fred Farkle";
		Entry entry = factory.newEntry();
		entry.setTitle("customer " + customerName);
		entry.setUpdated(new Date());
		entry.addAuthor("Apache Tuscany");
		// ID created by collection.
		Content content = abdera.getFactory().newContent();
		content.setContentType(Content.Type.TEXT);
		content.setValue(customerName);
		entry.setContentElement(content);

		RequestOptions opts = new RequestOptions();
		final String contentType = "application/atom+xml; type=entry"; 
		opts.setContentType(contentType);
		// AtomTestCaseUtils.printRequestHeaders( "Post request headers", "   ", opts );
		IRI colUri = new IRI(providerURI).resolve("customer");
		// res = client.post(colUri.toString() + "?test=foo", entry, opts);
		ClientResponse res = client.post(colUri.toString(), entry, opts);
		
	    // Assert response status code is 201-OK.
		// Assert response header Content-Type: application/atom+xml; charset=UTF-8
    	Assert.assertEquals(201, res.getStatus());
    	String returnedContentType = res.getContentType().toString().trim();
    	Assert.assertEquals(contentType, returnedContentType );

    	String eTag = res.getHeader( "ETag" );
    	if ( eTag != null)
    		lastId = eTag.substring( 1, eTag.length()-1);
    	
    	// AtomTestCaseUtils.printResponseHeaders( "Entry post response headers:", "   ", res );
    	// System.out.println("Entry post response content:");
    	// AtomTestCaseUtils.prettyPrint(abdera, res.getDocument());
	}

	@Test
	public void testXMLEntryGet() throws Exception {
		System.out.println(">>>ContentNegotiationTest.testXMLEntryGet");
		RequestOptions opts = new RequestOptions();
		opts.setHeader( "Accept", "application/atom+xml" );
		
		IRI colUri = new IRI(providerURI).resolve("customer");
		ClientResponse res = client.get(colUri.toString() + "/" + lastId, opts);
    	Assert.assertEquals(200, res.getStatus());
    	String returnedContentType = res.getContentType().toString().trim();
    	// Assert.assertEquals(contentType, returnedContentType );
		res.release();	    
	}

	@Test
	public void testJSONEntryGet() throws Exception {
		System.out.println(">>>ContentNegotiationTest.testJSONEntryGet");
		RequestOptions opts = new RequestOptions();
		opts.setHeader( "Accept", "application/json" );
		
		IRI colUri = new IRI(providerURI).resolve("customer");
		ClientResponse res = client.get(colUri.toString() + "/" + lastId, opts);
		try {
			Assert.assertEquals(200, res.getStatus());
			// Abdera 0.4 throws exception on getContentType with application/json.    	
			// System.out.println( "ContentNegotiationTest.testJSONEntryGet contentType=" + res.getContentType());
			String contentType = res.getHeader( "Content-Type");
			Assert.assertTrue( -1 < contentType.indexOf( "application/json" ));
			// Following is a poor man's JSONObject test to avoid dependency on JSON libs.
			// JSONObject jsonResp = new JSONObject(res.);
			// Assert.assertEquals(12345, jsonResp.getInt("result"));
			String responseBody = readResponse( res.getReader() );
			Assert.assertTrue( responseBody.startsWith( "{") );
			Assert.assertTrue( responseBody.endsWith( "}") );
			Assert.assertTrue( -1 < responseBody.indexOf( "\"id\"" ));
			Assert.assertTrue( -1 < responseBody.indexOf( "\"title\"" ));
			Assert.assertTrue( -1 < responseBody.indexOf( "\"updated\"" ));
			// AtomTestCaseUtils.printResponseHeaders( "JSON Entry response headers:", "   ", res );
			// System.out.println( "ContentNegotiationTest.testJSONEntryGet JSON entry body=" + responseBody );
		} finally {
			res.release();	    			
		}
	}

	@Test
    public void testXMLFeedGet() throws Exception {		
		System.out.println(">>>ContentNegotiationTest.testXMLFeedGet");
		RequestOptions opts = new RequestOptions();
		opts.setHeader( "Accept", "application/atom+xml" );
		
		// Atom feed request
		ClientResponse res = client.get(providerURI, opts);
		Assert.assertNotNull(res);
		try {
			// Asser feed provided since no predicates
			Assert.assertEquals(200, res.getStatus());
			Assert.assertEquals(ResponseType.SUCCESS, res.getType());
	    	// AtomTestCaseUtils.printResponseHeaders( "Feed response headers:", "   ", res );
	    	// System.out.println("Feed response content:");
	    	// AtomTestCaseUtils.prettyPrint(abdera, res.getDocument());

	    	// Perform other tests on feed.
			Document<Feed> doc = res.getDocument();
			Assert.assertNotNull( doc );
			Feed feed = doc.getRoot();
			Assert.assertNotNull( feed );
			// RFC 4287 requires non-null id, title, updated elements
			Assert.assertNotNull( feed.getId() );
			Assert.assertNotNull( feed.getTitle() );
			Assert.assertNotNull( feed.getUpdated() );
			// AtomTestCaseUtils.printFeed( "Feed values", "   ", feed );
		} finally {
			res.release();
		}
	}		

	@Test
    public void testJSONFeedGet() throws Exception {		
		System.out.println(">>>ContentNegotiationTest.testJSONFeedGet");
		RequestOptions opts = new RequestOptions();
		opts.setHeader( "Accept", "application/json" );
		
		// JSON feed request
		ClientResponse res = client.get(providerURI, opts);
		Assert.assertNotNull(res);
		try {
			// Assert feed provided since no predicates
			Assert.assertEquals(200, res.getStatus());
			// Abdera 0.4 throws exception on getContentType with application/json.    	
			// System.out.println( "ContentNegotiationTest.testJSONEntryGet contentType=" + res.getContentType());
			String contentType = res.getHeader( "Content-Type");
			Assert.assertTrue( -1 < contentType.indexOf( "application/json" ));
			// Following is a poor man's JSONObject test to avoid dependency on JSON libs.
			// JSONObject jsonResp = new JSONObject(res.);
			// Assert.assertEquals(12345, jsonResp.getInt("result"));
			String responseBody = readResponse( res.getReader() );
			Assert.assertTrue( responseBody.startsWith( "{") );
			Assert.assertTrue( responseBody.endsWith( "}") );
			Assert.assertTrue( -1 < responseBody.indexOf( "\"id\"" ));
			Assert.assertTrue( -1 < responseBody.indexOf( "\"title\"" ));
			Assert.assertTrue( -1 < responseBody.indexOf( "\"updated\"" ));
			Assert.assertTrue( -1 < responseBody.indexOf( "\"entries\"" ));
			// AtomTestCaseUtils.printResponseHeaders( "JSON Entry response headers:", "   ", res );
			// System.out.println( "ContentNegotiationTest.testJSONEntryGet JSON entry body=" + responseBody );
		} finally {
			res.release();
		}
	}
	
	protected String readResponse( Reader responseReader ) {
		if ( responseReader == null ) return ""; 
		StringBuffer sb = new StringBuffer(1024);
		try {
			int charValue = 0;
			while ((charValue = responseReader.read()) != -1) {
				//result = result + (char) charValue;
				sb.append((char)charValue);
			}
		} catch ( IOException e ) {
		}
		return sb.toString();
	}
}
