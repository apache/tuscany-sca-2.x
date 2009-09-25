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

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
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
public class MediaCollectionTestCase {
	public final static String providerURI = "http://localhost:8084/receipt";
	protected static SCADomain scaProviderDomain;
	protected static CustomerClient testService;
    protected static Abdera abdera;
    protected static AbderaClient client;
    protected static Parser abderaParser;    
    protected static String eTag;
    protected static Date lastModified;
    protected static String mediaId;
    protected static final SimpleDateFormat dateFormat = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss Z" ); // RFC 822 date time

	@BeforeClass
	public static void init() throws Exception {
		System.out.println(">>>MediaCollectionTestCase.init");
		scaProviderDomain = SCADomain.newInstance("org/apache/tuscany/sca/binding/atom/ReceiptProvider.composite");
		abdera = new Abdera();
		client = new AbderaClient(abdera);
		abderaParser = Abdera.getNewParser();
	}

    @AfterClass
    public static void destroy() throws Exception {
        System.out.println(">>>MediaCollectionTestCase.destroy");
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
	public void testMediaEntryPost() throws Exception {
    	// Pseudo Code (see APP (http://tools.ietf.org/html/rfc5023#section-9.6)
    	// Post request 
    	// POST /edit/ HTTP/1.1
        // Host: media.example.org
        // Content-Type: image/png
        // Slug: The Beach
        // Content-Length: nnn
        // ...binary data...
		
		// Testing of entry creation
		String receiptName = "Auto Repair Bill";
		String fileName = "target/test-classes/ReceiptToms.gif";
		File input = new File( fileName );
		boolean exists = input.exists();
		Assert.assertTrue( exists );		
		
        // Prepare HTTP post
        // PostMethod post = new PostMethod( colUri.toString() );
        PostMethod post = new PostMethod( providerURI );
        post.addRequestHeader( "Content-Type", "image/gif" );
        post.addRequestHeader( "Title", "Title " + receiptName + "" );
        post.addRequestHeader( "Slug", "Slug " + receiptName + "" );
        post.setRequestEntity( new InputStreamRequestEntity( new FileInputStream( input ), "image/gif" ) );
		
        // Get HTTP client
        HttpClient httpclient = new HttpClient();
        try {
            // Execute request
            int result = httpclient.executeMethod(post);
            // Pseudo Code (see APP (http://tools.ietf.org/html/rfc5023#section-9.6)
        	// Post response
            // Tuscany responds with proper media links. Note that the media is 
        	// stored in a different location than the media information which is
        	// stored in the Atom feed.
            // HTTP/1.1 201 Created
            // Display status code
            // System.out.println("Response status code: " + result + ", status text=" + post.getStatusText() );
        	Assert.assertEquals(201, result );
            // Display response
            // System.out.println("Response body: ");
            // System.out.println(post.getResponseBodyAsString()); // Warning: BodyAsString recommends BodyAsStream

            // Location: http://example.org/media/edit/the_beach.atom (REQUIRED)
            // System.out.println( "Response Location=" + post.getResponseHeader( "Location" ).getValue() + "." );
        	Header header = post.getResponseHeader( "Location" );
        	Assert.assertNotNull( header );
        	Assert.assertNotNull( header.getValue() );
            // ContentLocation: http://example.org/media/edit/the_beach.jpg (REQUIRED)
            // System.out.println( "Response Content-Location=" + post.getResponseHeader( "Content-Location" ).getValue() );
        	header = post.getResponseHeader( "Content-Location" );
        	Assert.assertNotNull( header );
        	Assert.assertNotNull( header.getValue() );      	
            // Content-Type: application/atom+xml;type=entry;charset="utf-8"
            // System.out.println( "Response Content-Type=" + post.getResponseHeader( "Content-Type" ).getValue());
        	header = post.getResponseHeader( "Content-Type" );
        	Assert.assertNotNull( header );
        	Assert.assertNotNull( header.getValue() );      	
            // Content-Length: nnn (OPTIONAL)
            // System.out.println( "Response Content-Length=" + post.getResponseHeader( "Content-Length" ).getValue() );
        	header = post.getResponseHeader( "Content-Length" );
        	Assert.assertNotNull( header );
        	Assert.assertNotNull( header.getValue() );      	
            // <?xml version="1.0"?>
            // <entry xmlns="http://www.w3.org/2005/Atom">
            //   <title>The Beach</title> (REQUIRED) 
            //   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id> (REQUIRED)
            //   <updated>2005-10-07T17:17:08Z</updated>
            //   <author><name>Daffy</name></author> 
            //   <summary type="text" /> (REQUIRED, OPTIONAL to populate
            //   <content type="image/png" src="http://media.example.org/the_beach.png"/>
            // <link rel="edit-media" href="http://media.example.org/edit/the_beach.png" />
            // <link rel="edit" href="http://example.org/media/edit/the_beach.atom" />
            // </entry>  		
            Document<Entry> document = abderaParser.parse( post.getResponseBodyAsStream() );
            Entry entry = document.getRoot();
            String title = entry.getTitle();
            // System.out.println( "mediaPost entry.title=" + title );
            Assert.assertNotNull( title );
            IRI id = entry.getId();
            // System.out.println( "mediaPost entry.id=" + id );
            Assert.assertNotNull( id );
            mediaId = id.toString();
            Assert.assertNotNull( mediaId ); // Save for put/update request
            Date updated = entry.getUpdated();
            // System.out.println( "mediaPost entry.updated=" + updated);
            Assert.assertNotNull( updated );
            String summary = entry.getSummary();
            // System.out.println( "mediaPost entry.summary=" + summary);
            Assert.assertNotNull( summary );
            IRI contentSrc = entry.getContentSrc();
            // System.out.println( "mediaPost entry.content.src=" + contentSrc + ", type=" + entry.getContentType());
            Assert.assertNotNull( contentSrc );
            Link editLink = entry.getEditLink();
    		// System.out.println( "mediaPost entry.editLink" + " rel=" + editLink.getRel() + ", href=" +  editLink.getHref() );
    		Assert.assertNotNull( editLink );
    		Assert.assertNotNull( editLink.getRel() );
    		Assert.assertNotNull( editLink.getHref() );
            Link editMediaLink = entry.getEditMediaLink();
    		// System.out.println( "mediaPost entry.editMediaLink" + " rel=" + editMediaLink.getRel() + ", href=" +  editMediaLink.getHref() );
    		Assert.assertNotNull( editMediaLink );
    		Assert.assertNotNull( editMediaLink.getRel() );
    		Assert.assertNotNull( editMediaLink.getHref() );

        } finally {
            // Release current connection to the connection pool once you are done
            post.releaseConnection();
        }
    }

	@Test
	public void testMediaEntryPutFound() throws Exception {
    	// Pseudo Code (see APP (http://tools.ietf.org/html/rfc5023#section-9.6)
		// Testing of entry update
		String receiptName = "Value Autoglass Bill";
		String fileName = "target/test-classes/ReceiptValue.jpg";
		File input = new File( fileName );
		boolean exists = input.exists();
		Assert.assertTrue( exists );		
		
        // Prepare HTTP put request
	    // PUT /edit/the_beach.png HTTP/1.1
	    // Host: media.example.org
	    // Content-Type: image/png
	    // Content-Length: nnn
	    // ...binary data...
        PutMethod put = new PutMethod( providerURI + "/" + mediaId );
        put.addRequestHeader( "Content-Type", "image/jpg" );
        put.addRequestHeader( "Title", "Title " + receiptName + "" );
        put.addRequestHeader( "Slug", "Slug " + receiptName + "" );
        put.setRequestEntity( 
        	new InputStreamRequestEntity( new FileInputStream( input ), "image/jpg" ) );
		
        // Get HTTP client
        HttpClient httpclient = new HttpClient();
        try {
            // Execute request
            int result = httpclient.executeMethod(put);
            // Pseudo Code (see APP (http://tools.ietf.org/html/rfc5023#section-9.6)
            // Display status code
            // System.out.println("Response status code: " + result + ", status text=" + put.getStatusText() );
        	Assert.assertEquals(200, result );
            // Display response. Should be empty for put.
            // System.out.println("Response body: ");
            // System.out.println(put.getResponseBodyAsString()); // Warning: BodyAsString recommends BodyAsStream
        } finally {
            // Release current connection to the connection pool once you are done
            put.releaseConnection();
        }
	}
	
	@Test
	public void testMediaEntryPutNotFound() throws Exception {
    	// Pseudo Code (see APP (http://tools.ietf.org/html/rfc5023#section-9.6)
		// Testing of entry update
		String receiptName = "Value Autoglass Bill";
		String fileName = "target/test-classes/ReceiptValue.jpg";
		File input = new File( fileName );
		boolean exists = input.exists();
		Assert.assertTrue( exists );		
		
        // Prepare HTTP put request
	    // PUT /edit/the_beach.png HTTP/1.1
	    // Host: media.example.org
	    // Content-Type: image/png
	    // Content-Length: nnn
	    // ...binary data...
        PutMethod put = new PutMethod( providerURI + "/" + mediaId + "-bogus" ); // Does not exist.
        put.addRequestHeader( "Content-Type", "image/jpg" );
        put.addRequestHeader( "Title", "Title " + receiptName + "" );
        put.addRequestHeader( "Slug", "Slug " + receiptName + "" );
        put.setRequestEntity( 
        	new InputStreamRequestEntity( new FileInputStream( input ), "image/jpg" ) );
		
        // Get HTTP client
        HttpClient httpclient = new HttpClient();
        try {
            // Execute request
            int result = httpclient.executeMethod(put);
            // Pseudo Code (see APP (http://tools.ietf.org/html/rfc5023#section-9.6)
            // Display status code
            // System.out.println("Response status code: " + result + ", status text=" + put.getStatusText() );
        	Assert.assertEquals(404, result );
            // Display response. Should be empty for put.
            // System.out.println("Response body: ");
            // System.out.println(put.getResponseBodyAsString()); // Warning: BodyAsString recommends BodyAsStream
        } finally {
            // Release current connection to the connection pool once you are done
            put.releaseConnection();
        }
	}
}
