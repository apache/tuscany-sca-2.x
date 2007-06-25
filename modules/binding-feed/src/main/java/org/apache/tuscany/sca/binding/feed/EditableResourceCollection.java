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

import java.io.InputStream;

import com.sun.syndication.feed.atom.Entry;

/**
 * Provides access to an editable resource collection using Atom.
 * 
 * @version $Rev$ $Date$
 */
public interface EditableResourceCollection extends ResourceCollection {

    /**
     * Creates a new entry.
     * 
     * @param entry
     * @return
     */
    Entry post(Entry entry);

    /**
     * Creates a new media entry
     * 
     * @param title
     * @param slug
     * @param contentType
     * @param media
     */
    Entry postMedia(String title, String slug, String contentType, InputStream media);

    /**
     * Retrieves an entry.
     * 
     * @param id
     * @return
     */
    Entry get(String id) throws ResourceNotFoundException;

    /**
     * Update an entry.
     * 
     * @param id
     * @param entry
     * @return
     */
    Entry put(String id, Entry entry) throws ResourceNotFoundException;

    /**
     * Update a media entry.
     * 
     * @param id
     * @param contentType
     * @param media
     * @return
     */
    Entry putMedia(String id, String contentType, InputStream media) throws ResourceNotFoundException;

    /**
     * Delete an entry.
     * 
     * @param id
     */
    void delete(String id) throws ResourceNotFoundException;

}
