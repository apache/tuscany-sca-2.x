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

package org.apache.tuscany.sca.domain.manager.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.Item;
import org.apache.tuscany.sca.data.collection.ItemCollection;
import org.apache.tuscany.sca.data.collection.NotFoundException;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * Implementation of a file collection service component. 
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@Service(ItemCollection.class)
public class FileCollectionImpl implements ItemCollection {
    
    private static final Logger logger = Logger.getLogger(FileCollectionImpl.class.getName());

    @Property
    public String directoryName;

    @Reference
    public DomainManagerConfiguration domainManagerConfiguration;
    
    /**
     * Initialize the component.
     */
    @Init
    public void initialize() throws IOException {
    }
    
    public Entry<String, Item>[] getAll() {
        logger.fine("getAll");
        
        String rootDirectory = domainManagerConfiguration.getRootDirectory();
        
        // Return all the files
        List<Entry<String, Item>> entries = new ArrayList<Entry<String, Item>>();
        File directory = new File(rootDirectory + "/" + directoryName);
        if (directory.exists()) {
            for (File file: directory.listFiles()) {
                if (file.getName().startsWith(".")) {
                    continue;
                }
                entries.add(entry(file.getName()));
            }
        }
        return entries.toArray(new Entry[entries.size()]);
    }

    public Item get(String key) throws NotFoundException {
        logger.fine("get " + key);
        return item(key);
    }

    public String post(String key, Item item) {
        throw new UnsupportedOperationException();
    }

    public void put(String key, Item item) throws NotFoundException {
        throw new UnsupportedOperationException();
    }

    public void delete(String key) throws NotFoundException {
        logger.fine("delete " + key);

        String rootDirectory = domainManagerConfiguration.getRootDirectory();
        File directory = new File(rootDirectory + "/" + directoryName);
        File file = new File(directory, key);
        if (file.exists()) {
            file.delete();
        } else {
            throw new NotFoundException(key);
        }
    }

    public Entry<String, Item>[] query(String queryString) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an entry representing a file.
     * 
     * @param fileName
     * @return
     */
    private static Entry<String, Item> entry(String fileName) {
        Entry<String, Item> entry = new Entry<String, Item>();
        entry.setKey(fileName);
        entry.setData(item(fileName));
        return entry;
    }

    /**
     * Returns an item representing a file.
     * 
     * @param fileName
     * @return
     */
    private static Item item(String fileName) {
        Item item = new Item();
        item.setTitle(fileName);
        item.setLink("/files/" + fileName);
        return item;
    }
}
