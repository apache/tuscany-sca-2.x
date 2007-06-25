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

package org.apache.tuscany.sca.binding.feed;

import org.osoa.sca.annotations.Remotable;

/**
 * Provides access to a resource collection feed.
 * 
 * @version $Rev$ $Date$
 */
@Remotable
public interface ResourceCollection {

    /**
     * Get an RSS or Atom feed for a collection of resources.
     * 
     * @param uri the uri of the feed
     * @return the RSS or Atom feed
     */
    com.sun.syndication.feed.atom.Feed getCollection();
}
