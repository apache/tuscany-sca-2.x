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
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Content;
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
 * Tests use of server provided feed entity tags for Atom binding in Tuscany.
 * Tests conditional gets (e.g. get if-none-match) or conditional posts (post if-match)
 * using entity tags and last modified entries in headers. 
 * Uses the SCA provided Provider composite to act as a server.
 * Uses the Abdera provided Client to act as a client.
 */
public class ProviderFeedEntityTagsTestCase {
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
		System.out.println(">>>ProviderFeedEntityTagsTestCase.init");
		scaProviderDomain = SCADomain.newInstance("org/apache/tuscany/sca/binding/atom/Provider.composite");
		abdera = new Abdera();
		client = new AbderaClient(abdera);
		abderaParser = Abdera.getNewParser();
	}

    @AfterClass
    public static void destroy() throws Exception {
        System.out.println(">>>ProviderFeedEntityTagsTestCase.destroy");
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
		System.out.println(">>>ProviderFeedEntityTagsTestCase.testFeedBasics");
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
			
			eTag = res.getHeader("ETag");
			Assert.assertNotNull( eTag );
			lastModified = res.getLastModified();
			Assert.assertNotNull( lastModified );
		} finally {
			res.release();
		}
	}		

	@Test
    public void testUnmodifiedGetIfMatch() throws Exception {		
		System.out.println(">>>ProviderFeedEntityTagsTestCase.testFeedUnmodifiedGetIfMatch");
		// Feed request with predicates
		RequestOptions opts = new RequestOptions();
		final String contentType = "application/atom+xml"; 
		opts.setContentType(contentType);
		opts.setHeader( "If-Match", eTag);
		
		ClientResponse res = client.get(providerURI, opts);
		Assert.assertNotNull(res);
		try {
	    	String thisETag = res.getHeader("ETag");
			Assert.assertNotNull( thisETag );
			Date thisLastModified = res.getLastModified();
			Assert.assertNotNull( thisLastModified );
                        
			// Should return 200 - Feed provided since it matches etag.
			Assert.assertEquals(200, res.getStatus());
			Assert.assertEquals(ResponseType.SUCCESS, res.getType());
	    	// AtomTestCaseUtils.printResponseHeaders( "Feed response headers:", "   ", res );
	    	// System.out.println("Feed response content:");
	    	// AtomTestCaseUtils.prettyPrint(abdera, res.getDocument());
		} finally {
			res.release();
		}
	}		

	@Test
    public void testUnmodifiedGetIfNoneMatch() throws Exception {		
		System.out.println(">>>ProviderFeedEntityTagsTestCase.testFeedUnmodifiedGetIfNoneMatch");
		// Feed request with predicates
		RequestOptions opts = new RequestOptions();
		final String contentType = "application/atom+xml"; 
		opts.setContentType(contentType);
		opts.setHeader( "If-None-Match", eTag);
		
		ClientResponse res = client.get(providerURI, opts);
		Assert.assertNotNull(res);
		try {
			// Should return 304 - Feed not provided since it matches ETag.
			Assert.assertEquals(304, res.getStatus());
		} finally {
			res.release();
		}
	}		

	@Test
    public void testUnmodifiedGetIfUnModified() throws Exception {		
		System.out.println(">>>ProviderFeedEntityTagsTestCase.testFeedUnmodifiedGetIfUnModified");
		// Feed request with predicates
		RequestOptions opts = new RequestOptions();
		final String contentType = "application/atom+xml"; 
		opts.setContentType(contentType);
		opts.setHeader( "If-Unmodified-Since", dateFormat.format( new Date() ));
		
		ClientResponse res = client.get(providerURI, opts);
		Assert.assertNotNull(res);
		try {
			// Should return 304 - Feed not provided since feed is modified since.
			Assert.assertEquals(304, res.getStatus());
		} finally {
			res.release();
		}
	}		

	@Test
    public void testUnmodifiedGetIfModified() throws Exception {		
		System.out.println(">>>ProviderFeedEntityTagsTestCase.testFeedUnmodifiedGetIfModified");
		// Feed request with predicates
		RequestOptions opts = new RequestOptions();
		final String contentType = "application/atom+xml"; 
		opts.setContentType(contentType);
		opts.setHeader( "If-Modified-Since", dateFormat.format( new Date( 0 ) ));
		
		ClientResponse res = client.get(providerURI, opts);
		Assert.assertNotNull(res);
		try {
			// Should return 200 - Feed provided since feed is changed.
			Assert.assertEquals(200, res.getStatus());
			Assert.assertEquals(ResponseType.SUCCESS, res.getType());

			String thisETag = res.getHeader("ETag");
			Assert.assertNotNull( thisETag );
			Date thisLastModified = res.getLastModified();
			Assert.assertNotNull( thisLastModified );                        
		} finally {
			res.release();
		}
	}		

	@Test
    public void testModifiedGetIfNoneMatch() throws Exception {		
		System.out.println(">>>ProviderFeedEntityTagsTestCase.testFeedModifiedGetIfNoneMatch");
		// Post some new content to the feed.
		Factory factory = abdera.getFactory();
		String customerName = "Fred Farkle";
		Entry entry = factory.newEntry();
		entry.setTitle("customer " + customerName);
		entry.setUpdated(new Date());
		entry.addAuthor("Apache Tuscany");
		Content content = abdera.getFactory().newContent();
		content.setContentType(Content.Type.TEXT);
		content.setValue(customerName);
		entry.setContentElement(content);

		RequestOptions opts = new RequestOptions();
		String contentType = "application/atom+xml; type=entry"; 
		opts.setContentType(contentType);
		IRI colUri = new IRI(providerURI).resolve("customer");
		ClientResponse res = client.post(colUri.toString(), entry, opts);
			
		// Feed request with predicates
		opts = new RequestOptions();
		contentType = "application/atom+xml"; 
		opts.setContentType(contentType);
		opts.setHeader( "If-None-Match", eTag);
		
		res = client.get(providerURI, opts);
		Assert.assertNotNull(res);
		try {
			// Should return 304 - Feed not provided since it matches ETag.
			Assert.assertEquals(304, res.getStatus());
	    	// AtomTestCaseUtils.printResponseHeaders( "Feed response headers:", "   ", res );
	    	// System.out.println("Feed response content:");
	    	// AtomTestCaseUtils.prettyPrint(abdera, res.getDocument());
		} finally {
			res.release();
		}
	}		

	@Test
    public void testModifiedGetIfMatch() throws Exception {		
		System.out.println(">>>ProviderFeedEntityTagsTestCase.testFeedModifiedGetIfMatch");
		// Feed request with predicates
		RequestOptions opts = new RequestOptions();
		final String contentType = "application/atom+xml"; 
		opts.setContentType(contentType);
		opts.setHeader( "If-Match", eTag);
		
		ClientResponse res = client.get(providerURI, opts);
		Assert.assertNotNull(res);
		try {
	    	String thisETag = res.getHeader("ETag");
			Assert.assertNotNull( thisETag );
			Date thisLastModified = res.getLastModified();
			Assert.assertNotNull( thisLastModified );
                        
			// Should return 200 - value since feed is changed
			Assert.assertEquals(200, res.getStatus());
			Assert.assertEquals(ResponseType.SUCCESS, res.getType());
			
	    	// AtomTestCaseUtils.printResponseHeaders( "Feed modified if-none-match response headers:", "   ", res );
	    	// System.out.println("Feed response content:");
	    	// AtomTestCaseUtils.prettyPrint(abdera, res.getDocument());
		} finally {
			res.release();
		}
	}		

	@Test
    public void testModifiedGetIfUnModified() throws Exception {		
		System.out.println(">>>ProviderFeedEntityTagsTestCase.testFeedUnmodifiedGetIfUnModified");
		// Feed request with predicates
		RequestOptions opts = new RequestOptions();
		final String contentType = "application/atom+xml"; 
		opts.setContentType(contentType);
		opts.setHeader( "If-Unmodified-Since", dateFormat.format( new Date() ));
		
		ClientResponse res = client.get(providerURI, opts);
		Assert.assertNotNull(res);
		try {
			// Should return 304 - Feed not provided since feed is modified since.			
			Assert.assertEquals(304, res.getStatus());
		} finally {
			res.release();
		}
	}		

	@Test
    public void testModifiedGetIfModified() throws Exception {		
		System.out.println(">>>ProviderFeedEntityTagsTestCase.testFeedUnmodifiedGetIfModified");
		// Feed request with predicates
		RequestOptions opts = new RequestOptions();
		final String contentType = "application/atom+xml"; 
		opts.setContentType(contentType);
		opts.setHeader( "If-Modified-Since", dateFormat.format( lastModified ));
		
		ClientResponse res = client.get(providerURI, opts);
		Assert.assertNotNull(res);
		try {
			// Should return 200 - Feed provided since feed is changed.
			Assert.assertEquals(200, res.getStatus());
			Assert.assertEquals(ResponseType.SUCCESS, res.getType());

			String thisETag = res.getHeader("ETag");
			Assert.assertNotNull( thisETag );
			Date thisLastModified = res.getLastModified();
			Assert.assertNotNull( thisLastModified );                        
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
		Collection collection = feed.getCollection();
		if ( collection == null ) {
			System.out.println( indent + "collection=null" );
		} else {
			System.out.println( indent + "collection=" + collection );
		}
		// System.out.println( indent + "collection size=" + feed.getCollection() );
		// for (Collection collection : workspace.getCollections()) {
		//    if (collection.getTitle().equals("customers")) {
		//       String expected = uri + "customers";
		//       String actual = collection.getResolvedHref().toString();
		//       assertEquals(expected, actual);
		//    }
		// }

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
