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
package org.apache.tuscany.sca.binding.rss.collection;

import org.osoa.sca.annotations.Remotable;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;


/**
 * Provides access to a collection of resources using RSS.
 * 
 * @version $Rev$ $Date$
 */
@Remotable
public interface Collection {

    /**
     * Get an RSS feed for a collection of resources.
     * 
     * @return the RSS feed
     */
    SyndFeed getFeed();

    /**
     * Get an RSS feed for a collection of resources resulting from a query.
     * 
     * @param queryString the query string
     * @return the RSS feed
     */
    SyndFeed query(String queryString);

    /**
     * Retrieves an RSS entry.
     * 
     * @param id The entry ID
     * @return The requested entry
     * @throws NotFoundException No entry found with the given ID
     */
    SyndEntry get(String id) throws NotFoundException;
}
