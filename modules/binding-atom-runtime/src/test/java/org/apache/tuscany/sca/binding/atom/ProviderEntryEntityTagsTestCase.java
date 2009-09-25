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

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.protocol.client.util.BaseRequestEntity;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests use of server provided entry entity tags for Atom binding in Tuscany.
 * Tests conditional gets (e.g. get if-none-match) or conditional posts (post if-match)
 * using entity tags or last modified header entries. 
 * Uses the SCA provided Provider composite to act as a server.
 * Uses the Abdera provided Client to act as a client.
 */
public class ProviderEntryEntityTagsTestCase {
	public final static String providerURI = "http://localhost:8084/customer";
	protected static SCADomain scaProviderDomain;
	protected static CustomerClient testService;
    protected static Abdera abdera;
    protected static AbderaClient client;
    protected static Parser abderaParser;    
    protected static String eTag;
    protected static Date lastModified;
    protected static final SimpleDateFormat dateFormat = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss Z" ); // RFC 822 date time

	@BeforeClass
	public static void init() throws Exception {
		System.out.println(">>>ProviderEntryEntityTagsTestCase.init");
		scaProviderDomain = SCADomain.newInstance("org/apache/tuscany/sca/binding/atom/Provider.composite");
		abdera = new Abdera();
		client = new AbderaClient(abdera);
		abderaParser = Abdera.getNewParser();
	}

    @AfterClass
    public static void destroy() throws Exception {
        System.out.println(">>>ProviderEntryEntityTagsTestCase.destroy");
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
	public void testEmptyCachePost() throws Exception {
		// Pseudo-code
		// 1) Example HTTP POST request (new entry put, new etag response)
		// User client post request       
		//        POST /myblog/entries HTTP/1.1
		//        Slug: First Post
		//        
		//        <?xml version="1.0" ?>
		//        <entry xmlns="http://www.w3.org/2005/Atom">
		//          <title>Atom-Powered Robots Run Amok</title>
		//          <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>
		//          <updated>2007-02-123T17:09:02Z</updated>
		//          <author><name>Captain Lansing</name></author>
		//          <content>It's something moving... solid metal</content>
		//        </entry>

		// Expected Atom server response (note unique ETag)
		//       HTTP/1.1 201 Created
		//       Date: Fri, 23 Feb 2007 21:17:11 GMT
		//       Content-Length: nnn
		//       Content-Type: application/atom+xml;type=entry
		//       Location: http://example.org/edit/first-post.atom
		//       Content-Location: http://example.org/edit/first-post.atom
		//       ETag: "e180ee84f0671b1"
		//       Last-Modified: Fri, 25 Jul 2008 14:36:44 -0500
		// 
		//        <?xml version="1.0" ?>
		//        <entry xmlns="http://www.w3.org/2005/Atom">
		//          <title>Atom-Powered Robots Run Amok</title>
		//          <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>
		//          <updated>2007-02-123T17:09:02Z</updated>
		//          <author><name>Captain Lansing</name></author>
		//          <content>It's something moving... solid metal</content>
		//        </entry>		
		
		// Testing of entry creation
		Factory factory = abdera.getFactory();
		String customerName = "Fred Farkle";
		Entry entry = factory.newEntry();
		entry.setTitle("customer " + customerName);
		entry.setUpdated(new Date());
		entry.addAuthor("Apache Tuscany");
		// ID created by collection.
        // entry.setId(id); // auto-provided
        // entry.addLink("" + id, "edit"); // auto-provided
        // entry.addLink("" + id, "alternate"); // auto-provided
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
		// Assert response header Location: http://example.org/edit/first-post.atom
		// Assert response header Content-Location: http://example.org/edit/first-post.atom
		// Assert response header ETag: "e180ee84f0671b1"
		// Assert response header Last-Modified: Fri, 25 Jul 2008 14:36:44 -0500	    
	    // Assert collection size is 1.
    	Assert.assertEquals(201, res.getStatus());
    	Assert.assertEquals(contentType, res.getContentType().toString().trim());
    	// Assert.assertNotNull( res.getLocation().toString() );
    	// Assert.assertEquals( "", res.getContentLocation().toString() );
    	// Save eTag for subsequent tests;
    	eTag = res.getHeader( "ETag" );
    	Assert.assertNotNull( eTag );     	
    	lastModified = res.getLastModified();
    	Assert.assertNotNull(lastModified);
	}

	@Test
	public void testDirtyCachePut() throws Exception {
		// 2) Conditional PUT request (post with etag. entry provided is stale)
		// User client PUT request
		//        PUT /edit/first-post.atom HTTP/1.1
		// >      If-Match: "e180ee84f0671b1"
		// 
		//        <?xml version="1.0" ?>
		//        <entry xmlns="http://www.w3.org/2005/Atom">
		//         <title>Atom-Powered Robots Run Amok</title>
		//         <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>
		//         <updated>2007-02-24T16:34:06Z</updated>
		//         <author><name>Captain Lansing</name></author>
		//         <content>Update: it's a hoax!</content>
		//       </entry>
		// Testing of entry creation
		Factory factory = abdera.getFactory();
		String customerName = "Molly Ringwald";
		Entry entry = factory.newEntry();
		entry.setTitle("customer " + customerName);
		entry.setUpdated( new Date());
		entry.addAuthor("Apache Tuscany");
		String id = eTag.substring( 1, eTag.length()-1);
        entry.setId(id); // auto-provided
        // entry.addLink("" + id, "edit"); // auto-provided
        // entry.addLink("" + id, "alternate"); // auto-provided
		Content content = abdera.getFactory().newContent();
		content.setContentType(Content.Type.TEXT);
		content.setValue(customerName);
		entry.setContentElement(content);

		RequestOptions opts = new RequestOptions();
		final String contentType = "application/atom+xml; type=entry"; 
		opts.setContentType(contentType);
		opts.setHeader( "If-None-Match", eTag);
		
		// AtomTestCaseUtils.printRequestHeaders( "Put request headers", "   ", opts );
		IRI colUri = new IRI(providerURI).resolve("customer");
		// res = client.post(colUri.toString() + "?test=foo", entry, opts);
	    id = eTag.substring( 1, eTag.length()-1);
		// Warning. AbderaClient.put(String uri,Base base,RequestOptions options) caches on the client side.
		// ClientResponse res = client.put(colUri.toString() + id, entry, opts);
		ClientResponse res = client.put(colUri.toString() + "/" + id, new BaseRequestEntity( entry ), opts);
		// Expected Atom server response (item was edited by another user)
		// >      HTTP/1.1 412 Precondition Failed
		//       Date: Sat, 24 Feb 2007 16:34:11 GMT

	    // If-Match Assert response status code is 412. Precondition failed.
	    // If-None-Match Assert response status code is 200. OK
    	Assert.assertEquals(200, res.getStatus());
    	// Put provides null body and no etags.
    	res.release();
	}
	
	@Test
	public void testETagMissGet() throws Exception {
		// 4) Conditional GET example (get with etag. etag not in cache)
		// User client GET request
		//       GET /edit/first-post.atom HTTP/1.1
		// >      If-None-Match: "e180ee84f0671b1"

		RequestOptions opts = new RequestOptions();
		final String contentType = "application/atom+xml; type=entry"; 
		opts.setContentType(contentType);
		opts.setHeader( "If-None-Match", "123456");
		opts.setHeader( "Pragma", "no-cache"); // turn off client caching
		
		IRI colUri = new IRI(providerURI).resolve("customer");
		// res = client.post(colUri.toString() + "?test=foo", entry, opts);
		String id = eTag.substring( 1, eTag.length()-1);
		// Warning. AbderaClient.put(String uri,Base base,RequestOptions options) caches on the client side.
		// ClientResponse res = client.put(colUri.toString() + id, entry, opts);
		ClientResponse res = client.get(colUri.toString() + "/" + id, opts);
		// Expected Atom server response (item was edited by another user)
		// >      HTTP/1.1 412 Precondition Failed
		//       Date: Sat, 24 Feb 2007 16:34:11 GMT

		// Atom server response (item was up to date)
		// >      HTTP/1.1 200 OK
		//        Date: Sat, 24 Feb 2007 13:17:11 GMT
		// >      ETag: "bb4f5e86e92ddb8549604a0df0763581"
		// >      Last-Modified: Mon, 28 Jul 2008 10:25:37 -0500

	    // Assert response status code is 200 OK.
		// Assert header Content-Type: application/atom+xml;type=entry
		// Assert header Location: http://example.org/edit/first-post.atom
		// Assert header Content-Location: http://example.org/edit/first-post.atom
		// Assert header ETag: "555555" (etag response != etag request)
		// Assert header Last-Modified: Fri, 25 Jul 2008 14:36:44 -0500
    	Assert.assertEquals(200, res.getStatus());
    	Assert.assertEquals(contentType, res.getContentType().toString().trim());
    	// Assert.assertNotNull( res.getLocation().toString() );
    	// Assert.assertEquals( "", res.getContentLocation().toString() );
    	Assert.assertNotNull( res.getHeader( "ETag" ) );     	
    	lastModified = res.getLastModified();
    	Assert.assertNotNull(lastModified);
		res.release();
	}

	@Test
	public void testETagHitGet() throws Exception {
		// 3) Conditional GET example (get with etag. etag match)
		// User client GET request
		//       GET /edit/first-post.atom HTTP/1.1
		// >      If-None-Match: "e180ee84f0671b1"

		RequestOptions opts = new RequestOptions();
		final String contentType = "application/atom+xml; type=entry"; 
		opts.setContentType(contentType);
		opts.setHeader( "If-None-Match", eTag);
		opts.setHeader( "Pragma", "no-cache"); // turn off client caching		
		
		IRI colUri = new IRI(providerURI).resolve("customer");
		// res = client.post(colUri.toString() + "?test=foo", entry, opts);
		String id = eTag.substring( 1, eTag.length()-1);
		// Warning. AbderaClient.put(String uri,Base base,RequestOptions options) caches on the client side.
		// ClientResponse res = client.put(colUri.toString() + id, entry, opts);
		ClientResponse res = client.get(colUri.toString() + "/" + id, opts);
		// Atom server response (item was up to date)
		// >      HTTP/1.1 304 Not Modified
		//       Date: Sat, 24 Feb 2007 13:17:11 GMT

	    // Assert response status code is 304 Not Modified.
		// Assert header ETag: "e180ee84f0671b1"
		// Assert header Last-Modified: Fri, 25 Jul 2008 14:36:44 -0500
    	// Assert.assertEquals(304, res.getStatus());
		res.release();	    
	}


	@Test
	public void testUpToDateGet() throws Exception {
		// 3) Conditional GET example (get with If-Mod. entry is up to date)
		// User client GET request
		//       GET /edit/first-post.atom HTTP/1.1
		// >      If-Modified-Since: Sat, 29 Oct 2025 19:43:31 GMT
		RequestOptions opts = new RequestOptions();
		final String contentType = "application/atom+xml; type=entry"; 
		opts.setContentType(contentType);
		opts.setHeader( "If-Modified-Since", "Sat, 29 Oct 2025 19:43:31 GMT"); // "EEE, dd MMM yyyy HH:mm:ss Z // RFC 822 Date
		opts.setHeader( "Pragma", "no-cache"); // turn off client caching
		
		IRI colUri = new IRI(providerURI).resolve("customer");
		// res = client.post(colUri.toString() + "?test=foo", entry, opts);
		String id = eTag.substring( 1, eTag.length()-1);
		// Warning. AbderaClient.put(String uri,Base base,RequestOptions options) caches on the client side.
		// ClientResponse res = client.put(colUri.toString() + id, entry, opts);
		// Warning. AbderaClient.put(String uri,Base base,RequestOptions options) caches on the client side.
		// ClientResponse res = client.get(colUri.toString() + "/" + id, opts);
		ClientResponse res = client.execute( "GET", colUri.toString(), (BaseRequestEntity)null, opts);

		// Atom server response (item was up to date)
		// >      HTTP/1.1 304 Not Modified
		//       Date: Sat, 24 Feb 2007 13:17:11 GMT

	    // Assert response status code is 304 Not Modified.
    	Assert.assertEquals(304, res.getStatus());
		res.release();	    
	}

	@Test
	public void testOutOfDateGet() throws Exception {
		// 4) Conditional GET example (get with If-Mod. entry is not to date)
		// User client GET request
		//       GET /edit/first-post.atom HTTP/1.1
		// >      If-Modified-Since: Sat, 29 Oct 1844 19:43:31 GMT
		RequestOptions opts = new RequestOptions();
		final String contentType = "application/atom+xml; type=entry"; 
		opts.setContentType(contentType);
		opts.setHeader( "If-Modified-Since", "Sat, 29 Oct 1844 19:43:31 GMT"); // "EEE, dd MMM yyyy HH:mm:ss Z // RFC 822 Date
		opts.setHeader( "Pragma", "no-cache"); // turn off client caching
		
		IRI colUri = new IRI(providerURI).resolve("customer");
		// res = client.post(colUri.toString() + "?test=foo", entry, opts);
		String id = eTag.substring( 1, eTag.length()-1);
		// Warning. AbderaClient.put(String uri,Base base,RequestOptions options) caches on the client side.
		// ClientResponse res = client.put(colUri.toString() + id, entry, opts);
		ClientResponse res = client.get(colUri.toString() + "/" + id, opts);

		// Atom server response (item was up to date)
		// >      HTTP/1.1 200 OK
		//        Date: Sat, 24 Feb 2007 13:17:11 GMT
		// >      ETag: "bb4f5e86e92ddb8549604a0df0763581"
		// >      Last-Modified: Mon, 28 Jul 2008 10:25:37 -0500

	    // Assert response status code is 200 OK.
		// Assert header ETag: "e180ee84f0671b1"
		// Assert header Last-Modified: Greater than If-Mod	    
    	Assert.assertEquals(200, res.getStatus());
    	Assert.assertEquals(contentType, res.getContentType().toString().trim());
    	// Assert.assertNotNull( res.getLocation().toString() );
    	// Assert.assertEquals( "", res.getContentLocation().toString() );
    	Assert.assertNotNull( res.getHeader( "ETag" ) );     	
    	lastModified = res.getLastModified();
    	Assert.assertNotNull(lastModified);
		res.release();
	}
	
	@Test
	public void testUpToDateUnModGet() throws Exception {
		// 3) Conditional GET example (get with If-Unmod. entry is not modified (< predicate date).
		// User client GET request
		//       GET /edit/first-post.atom HTTP/1.1
		// >      If-Unmodified-Since: Sat, 29 Oct 2025 19:43:31 GMT
		RequestOptions opts = new RequestOptions();
		final String contentType = "application/atom+xml; type=entry"; 
		opts.setContentType(contentType);
		opts.setHeader( "If-Unmodified-Since", "Sat, 29 Oct 2050 19:43:31 GMT" );
		opts.setHeader( "Pragma", "no-cache"); // turn off client caching
		
		IRI colUri = new IRI(providerURI).resolve("customer");
		// res = client.post(colUri.toString() + "?test=foo", entry, opts);
		String id = eTag.substring( 1, eTag.length()-1);
		// Warning. AbderaClient.put(String uri,Base base,RequestOptions options) caches on the client side.
		// ClientResponse res = client.put(colUri.toString() + id, entry, opts);
		ClientResponse res = client.get(colUri.toString() + "/" + id, opts);

		// Atom server response (item was up to date)
		// >      HTTP/1.1 200 OK
		//        Date: Sat, 24 Feb 2007 13:17:11 GMT
		// >      ETag: "bb4f5e86e92ddb8549604a0df0763581"
		// >      Last-Modified: Mon, 28 Jul 2008 10:25:37 -0500

	    // Assert response status code is 200 OK.
		// Assert header Content-Type: application/atom+xml;type=entry
		// Assert header Location: http://example.org/edit/first-post.atom
		// Assert header Content-Location: http://example.org/edit/first-post.atom
		// Assert header ETag: "e180ee84f0671b1"
		// Assert header Last-Modified: Less than If-Unmod	    
    	Assert.assertEquals(200, res.getStatus());
		res.release();	        
	}

	@Test
	public void testOutOfDateUnModGet() throws Exception {
		// 4) Conditional GET example (get with If-Unmod. entry is modified (> predicate date)
		// User client GET request
		//       GET /edit/first-post.atom HTTP/1.1
		//        Host: example.org
		// >      If-Unmodified-Since: Sat, 29 Oct 1844 19:43:31 GMT
		RequestOptions opts = new RequestOptions();
		final String contentType = "application/atom+xml; type=entry"; 
		opts.setContentType(contentType);
		opts.setHeader( "If-Unmodified-Since", "Sat, 29 Oct 1844 19:43:31 GMT" );
		opts.setHeader( "Pragma", "no-cache"); // turn off client caching
		
		IRI colUri = new IRI(providerURI).resolve("customer");
		// res = client.post(colUri.toString() + "?test=foo", entry, opts);
		String id = eTag.substring( 1, eTag.length()-1);
		// Warning. AbderaClient.put(String uri,Base base,RequestOptions options) caches on the client side.
		// ClientResponse res = client.put(colUri.toString() + id, entry, opts);
		ClientResponse res = client.get(colUri.toString() + "/" + id, opts);

		// Atom server response (item was up to date)
		// >      HTTP/1.1 304 Not Modified
		//       Date: Sat, 24 Feb 2007 13:17:11 GMT

	    // Assert response status code is 304 Not Modified.
    	Assert.assertEquals(304, res.getStatus());
		res.release();	    
	}
}
