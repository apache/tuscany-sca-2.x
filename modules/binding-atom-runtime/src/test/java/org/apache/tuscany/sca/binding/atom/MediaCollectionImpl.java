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
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.tuscany.sca.binding.atom.collection.MediaCollection;
import org.apache.tuscany.sca.binding.atom.collection.NotFoundException;
import org.osoa.sca.annotations.Scope;

@Scope("COMPOSITE")
public class MediaCollectionImpl implements MediaCollection {
    private final Abdera abdera = new Abdera();
    private Map<String, Entry> entries = new HashMap<String, Entry>();
    private Map<String, String> mediaFiles = new HashMap<String, String>();
    public Date lastModified = new Date();
    
    public Entry post(Entry entry) {
        System.out.println(">>> MediaCollectionImpl.post entry=" + entry.getTitle());

        if(!("Exception_Test".equalsIgnoreCase(entry.getTitle())))
        {
           String id = "urn:uuid:customer-" + UUID.randomUUID().toString();
           entry.setId(id);

           entry.addLink("" + id, "edit");
           entry.addLink("" + id, "alternate");
           Date now = new Date();
           entry.setUpdated(now);
           lastModified = now;
           entries.put(id, entry);

            System.out.println(">>> MediaCollectionImpl.post return id=" + id);

            return entry;

        }
        else
        {
        	throw new IllegalArgumentException("Exception in Post method");
        }
    }

    public Entry get(String id) {
        System.out.println(">>> MediaCollectionImpl.get id=" + id);
        return entries.get(id);
    }

    public void put(String id, Entry entry) throws NotFoundException {
        System.out.println(">>> MediaCollectionImpl.put id=" + id + " entry=" + entry.getTitle());
        if(entries.containsKey(id)){
        	Date now = new Date();
        	entry.setUpdated(now);
        	lastModified = now;
            entries.put(id, entry);
        }
        else {
        	throw new NotFoundException();
        }
     }

    public void delete(String id) throws NotFoundException {
        System.out.println(">>> MediaCollectionImpl.delete id=" + id);
        if(entries.containsKey(id)){
        	entries.remove(id);
        	lastModified = new Date();
        }
        else {
        	throw new NotFoundException();
		}
     }

    public Feed getFeed() {
        System.out.println(">>> MediaCollectionImpl.getFeed");

        Feed feed = this.abdera.getFactory().newFeed();
        feed.setId("customers" + this.hashCode() ); // provide unique id for feed instance.
        feed.setTitle("customers");
        feed.setSubtitle("This is a sample feed");
        feed.setUpdated(lastModified);
        feed.addLink("");
        feed.addLink("", "self");

        for (Entry entry : entries.values()) {
            feed.addEntry(entry);
        }

        return feed;
    }

    public Feed query(String queryString) {
        System.out.println(">>> MediaCollectionImpl.query collection " + queryString);
        return getFeed();
    }

    // This method used for testing.
    protected void testPut(String value) {
        String id = "urn:uuid:customer-" + UUID.randomUUID().toString();

        Entry entry = abdera.getFactory().newEntry();
        entry.setId(id);
        entry.setTitle("customer " + value);

        Content content = this.abdera.getFactory().newContent();
        content.setContentType(Content.Type.TEXT);
        content.setValue(value);

        entry.setContentElement(content);

        entry.addLink("" + id, "edit");
        entry.addLink("" + id, "alternate");

        entry.setUpdated(new Date());

        entries.put(id, entry);
        System.out.println(">>> id=" + id);
    }

    // MediaCollection role
    public Entry postMedia(String title, String slug, String contentType, InputStream media) {
        System.out.println(">>> MediaCollectionImpl.postMedia title=" + title + ", slug=" + slug + ", contentType=" + contentType );

        Factory factory = abdera.getFactory();
        Entry entry = factory.newEntry();
        // Must provide entry to media as per Atom Pub spec (http://tools.ietf.org/html/rfc5023#section-9.6)
        // <?xml version="1.0"?>
        // <entry xmlns="http://www.w3.org/2005/Atom">
        //   <title>The Beach</title> (REQUIRED) 
        //   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id> (REQUIRED)
        //   <updated>2005-10-07T17:17:08Z</updated>
        //   <summary type="text" /> (REQUIRED, OPTIONAL to populate
        //   <content type="image/png" src="http://media.example.org/the_beach.png"/>
        // <link rel="edit-media" href="http://media.example.org/edit/the_beach.png" />
        // <link rel="edit" href="http://example.org/media/edit/the_beach.atom" />
        // </entry>  		

        // Normalize title
		entry.setTitle( title );
        String normalTitle = title.replace( " ", "_" );
        String hostURL = "http://media.example.org/";
        int lastDelimiterPos = contentType != null ? contentType.lastIndexOf( "/" ) : -1;
        String extension = "";
        if ( lastDelimiterPos != -1 ) {
        	extension = contentType.substring( lastDelimiterPos + 1 );
        } else {
        	extension = contentType;
        }
        long mediaLength = -1;
        try {
        	mediaLength = media.skip( Long.MAX_VALUE );
        } catch ( IOException e ){}
        
        // A true implementation would store the media to a repository, e.g. file system.
        // This implementation record's the id and the location.
        String id = "urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a-" + mediaFiles.size();
        String reposLocation = hostURL + "edit/" + normalTitle;
        mediaFiles.put( id, reposLocation );

        // Build entry for media link.
		entry.setUpdated( new Date() );
		entry.setId( id );
		// Convention. Return header properties as key values.
		entry.setSummary( "Content-Type=" + contentType + ",Content-Length=" + mediaLength  );
		entry.setContent( new IRI( hostURL + normalTitle + "." + extension ), contentType );
		entry.addLink( reposLocation + ".atom", "edit" );
		entry.addLink( reposLocation + "." + extension, "edit-media" );
		return entry;  	
    }

    public void putMedia(String id, String contentType, InputStream media) throws NotFoundException {
        System.out.println(">>> MediaCollectionImpl.putMedia id=" + id + ", contentType=" + contentType );

        // Must responsd with success or not found as per Atom Pub spec (http://tools.ietf.org/html/rfc5023#section-9.6)
        // Body is null.
        if ( !id.endsWith( "0" ) )
        	throw new NotFoundException( "Media at id=" + id + " not found." );
        
        // A true implementation would update the media in the media repository.
    }

}
