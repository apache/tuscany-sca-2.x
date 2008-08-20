package feed;

import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.apache.tuscany.sca.host.embedded.SCADomain;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Collection;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.util.EntityTag;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.apache.abdera.parser.Parser;

/**
 * Tests use of server provided feed entity tags for Atom binding in Tuscany.
 * Tests conditional gets (e.g. get if-none-match) or conditional posts (post if-match)
 * using entity tags and last modified entries in headers. 
 * Uses the SCA provided Provider composite to act as a server.
 * Uses the Abdera provided Client to act as a client.
 */
public class FeedAggregatorTest {
	public final static String providerURI = "http://localhost:8083/atomAggregator";
	protected static SCADomain scaProviderDomain;
    protected static Abdera abdera;
    protected static AbderaClient client;
    protected static Parser abderaParser;    
    protected static String eTag;
    protected static Date lastModified;
    protected static long contentLength;
    protected static int numberEntries;
    protected static final SimpleDateFormat dateFormat = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss Z" ); // RFC 822 date time

	@BeforeClass
	public static void init() throws Exception {
		try {
		System.out.println(">>>FeedAggregatorTest.init");
		scaProviderDomain = SCADomain.newInstance("FeedAggregator.composite");
		abdera = new Abdera();
		client = new AbderaClient(abdera);
		abderaParser = Abdera.getNewParser();
		} catch ( Throwable e ) {
			System.out.println( e );
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void destroy() throws Exception {
		System.out.println(">>>FeedAggregatorTest.destroy");
		scaProviderDomain.close();
	}

	@Test
	public void testPrelim() throws Exception {
		Assert.assertNotNull(scaProviderDomain);
		Assert.assertNotNull( client );
	}
			
	@Test
    public void testFeedBasics() throws Exception {		
		System.out.println(">>>FeedAggregatorTest.testFeedBasics");
		RequestOptions opts = new RequestOptions();
		// Normal feed request
		ClientResponse res = client.get(providerURI, opts);
		Assert.assertNotNull(res);
		try {
			// Asser feed provided since no predicates
			Assert.assertEquals(200, res.getStatus());
			Assert.assertEquals(ResponseType.SUCCESS, res.getType());
	    	// AtomTestCaseUtils.printResponseHeaders( "Feed response headers:", "   ", res );

	    	// Perform other tests on feed.
	    	contentLength = getContentLength( res );
	    	System.out.println( "FeedAggregatorTest.testFeedBasics full contentLength=" + contentLength );	    	

	    	Document<Feed> doc = res.getDocument();
			Assert.assertNotNull( doc );
			Feed feed = doc.getRoot();
			Assert.assertNotNull( feed );
			
			// printFeed( "Feed values", "   ", feed );
			// RFC 4287 requires non-null id, title, updated elements
			Assert.assertNotNull( feed.getId() );
			Assert.assertNotNull( feed.getTitle() );
			Assert.assertNotNull( feed.getUpdated() );
			
			eTag = res.getHeader("ETag");
			Assert.assertNotNull( eTag );
			lastModified = res.getLastModified();
			Assert.assertNotNull( lastModified );
			
			numberEntries = getEntryCount( feed );
			System.out.println( "FeedAggregatorTest.testFeedBasics number entries=" + numberEntries );

			// printFeed( "Aggregated Feed Contents:", "   ", feed );
			// System.out.println( "FeedAggregatorTest.testFeedBasics feed=" + feed );
            // printResponseHeaders( "Aggregated Feed response headers:", "   ", res );
	    	// System.out.println("Aggregated Feed response body:");
	    	// prettyPrint(abdera, res.getDocument());	    	
	    	// printEntryUpdates( "Aggregated Feed feed updates", "   ", feed );
		} finally {
			res.release();
		}
	}		

	@Test
    public void testUnmodifiedGetIfModified() throws Exception {		
		System.out.println(">>>FeedAggregatorTest.testFeedUnmodifiedGetIfModified");
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
			
			// Entry count and body size should be equal to basic request
	    	long thisContentLength = getContentLength( res );
    	
	    	Document<Feed> doc = res.getDocument();
			Assert.assertNotNull( doc );
			Feed feed = doc.getRoot();
			Assert.assertNotNull( feed );
	    	int thisNumberEntries = getEntryCount( feed );
			// System.out.println( "FeedAggregatorTest.UnmodifiedGetIfModified number entries=" + numberEntries + ", this number entries=" + thisNumberEntries ) ;
		} finally {
			res.release();
		}
	}		

	@Test
    public void testUnmodifiedGetIfUnModified() throws Exception {		
		System.out.println(">>>FeedAggregatorTest.testFeedUnmodifiedGetIfUnModified");
		// Feed request with predicates
		RequestOptions opts = new RequestOptions();
		final String contentType = "application/atom+xml"; 
		opts.setContentType(contentType);
		opts.setHeader( "If-Unmodified-Since", dateFormat.format( new Date( 0 ) ));
		
		ClientResponse res = client.get(providerURI, opts);
		Assert.assertNotNull(res);
		try {
			// Should return 304 - Feed not provided since feed is modified since.
			Assert.assertEquals(304, res.getStatus());

			// Entry count and body size should be equal to basic request
	    	long thisContentLength = getContentLength( res );
			System.out.println( "FeedAggregatorTest.UnModifiedGetIfUnModified saved " + (contentLength - thisContentLength) + " bytes of network traffic due to caching.");
		} finally {
			res.release();
		}
	}		

	/** Print feed vital fields. */
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
		System.out.println( indent + "self link=" + feed.getSelfLink() );
		Collection collection = feed.getCollection();
		if ( collection == null ) {
			System.out.println( indent + "collection=null" );
		} else {
			System.out.println( indent + "collection=" + collection );
		}
	}

	/* Print headers of request. */
    public static void printRequestHeaders( String title, String indent, RequestOptions request ) {
     	System.out.println( title );
     	if ( request == null ) {
     		System.out.println( indent + " request is null");
     		return;
     	}
     	String [] headerNames = request.getHeaderNames();
     	for ( String headerName: headerNames) {
     		String header = request.getHeader(headerName);
    	       System.out.println( indent + " header name,value=" + headerName + "," + header );	
     	}    	               	           
     }
    
	/* Print headers of response. */
     public static void printResponseHeaders( String title, String indent, ClientResponse response ) {
      	System.out.println( title );
     	if ( response == null ) {
     		System.out.println( indent + " response is null");
     		return;
     	}
     	String [] headerNames = response.getHeaderNames();
     	for ( String headerName: headerNames) {
     	    String header = response.getHeader(headerName);
     	    System.out.println( indent + " header name,value=" + headerName + "," + header );
     	}
     	               	           
     }

    /** Pretty print the document body. */
	public static void prettyPrint(Abdera abdera, Base doc) throws IOException {
        WriterFactory factory = abdera.getWriterFactory();
        Writer writer = factory.getWriter("prettyxml");
        writer.writeTo(doc, System.out);
        System.out.println();
 	}

	/** Print the updated elements of the feed entries. */
	public static void printEntryUpdates( String title, String indent, Feed feed ) {
		if ( feed == null ) {
			System.out.println( title + " feed is null");
			return;
		}

		System.out.println( title );
		List<Entry> entries = feed.getEntries();
		if ( entries == null ) {
			System.out.println( indent + " null entries");			
		}
		System.out.println( indent + "entries size=" + entries.size());			
		
		int i = 0;
		for ( Entry entry: entries ) {
			String entryTitle = entry.getTitle();
			if (( entryTitle != null ) && ( entryTitle.length() > 20 ))
				entryTitle = entryTitle.substring( 0, 20 );
	        // System.out.println( indent + i++ + ": title=\"" + entryTitle + 
	        // 		"\", updated=" + entry.getUpdated() + ", published=" + entry.getPublished() );
	        System.out.println( indent + i++ + ": title=\"" + entryTitle + 
	        		"\", updated=" + entry.getUpdated() );
		}
	}
	
	/** Get the length of the response body content. */
	public static long getContentLength( ClientResponse response ) {
		// getContentLenght returns -1
    	// contentLength = response.getContentLength();
		try { 
			Reader reader = response.getReader();
			long actualSkip = reader.skip( Long.MAX_VALUE );
			return actualSkip;		
		} catch ( IOException e ) {
		}
		return -1L;
	}

	/** Get a count of entries in the feed. */
	public static int getEntryCount( Feed feed ) {
		if ( feed == null ) {
			return 0;
		}

		List<Entry> entries = feed.getEntries();
		if ( entries == null ) {
			return 0;			
		}
		return entries.size();
	}

	/** Given a feed, determine the median point of the entries.
	 * Use the updated field of the entries to determine median.
	 * @param feed
	 * @return
	 */
	public static Date getUpdatedMedian( Feed feed ) {
		Date sentinal = null;
		if ( feed == null ) {
			return sentinal;
		}

		List<Entry> entries = feed.getEntries();
		if ( entries == null ) {
			return sentinal;			
		}
		int size = entries.size();
		if( size == 0 ) {
			return sentinal;
		}
		// System.out.println( "getUpdatedMedian entries size=" + entries.size());
		ArrayList<Date> updates = new ArrayList<Date>( size );
		
		for ( Entry entry: entries ) {
			Date entryUpdated = entry.getUpdated();
			if ( entryUpdated == null ) {
				entryUpdated = new Date( 0 );
			}
			updates.add( entryUpdated );
		}
		Collections.sort( updates );
		// System.out.println( "getUpdatedMedian entry min update=" + updates.get( 0 ));
		// System.out.println( "getUpdatedMedian entry max update=" + updates.get( size - 1 ));
		Date median = updates.get( size/2 );
		// System.out.println( "getUpdatedMedian entry max median=" + median );
		return median;
	}	
}
