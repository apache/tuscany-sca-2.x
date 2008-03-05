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

package org.apache.tuscany.sca.workspace.admin.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.implementation.data.collection.Entry;
import org.apache.tuscany.sca.implementation.data.collection.Item;
import org.apache.tuscany.sca.implementation.data.collection.NotFoundException;
import org.apache.tuscany.sca.workspace.admin.ContributionFileCollection;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * Implementation of a file collection service component. 
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@Service(ContributionFileCollection.class)
public class ContributionFileCollectionImpl implements ContributionFileCollection {
    
    @Property
    public String directoryName;

    private File files;
    
    /**
     * Initialize the component.
     */
    @Init
    public void initialize() throws IOException {
        files = new File(directoryName);
        if (!files.exists()) {
            files.mkdirs();
        }
    }
    
    public Entry<String, Item>[] getAll() {
        // Return all the contributions
        List<Entry<String, Item>> entries = new ArrayList<Entry<String, Item>>();
        for (File file: files.listFiles()) {
            if (file.getName().startsWith(".")) {
                continue;
            }
            Entry<String, Item> entry = new Entry<String, Item>();
            entry.setKey(file.getName());
            Item item = new Item();
            item.setTitle(file.getName());
            item.setLink("/files/" + file.getName());
            entry.setData(item);
            entries.add(entry);
        }
        return entries.toArray(new Entry[entries.size()]);
    }

    public Item get(String key) throws NotFoundException {
        throw new NotFoundException(key);
    }

    public String post(String key, Item item) {
        throw new UnsupportedOperationException();
    }

    public void put(String key, Item item) throws NotFoundException {
        throw new UnsupportedOperationException();
    }

    public void delete(String key) throws NotFoundException {
        File file = new File(files, key);
        if (file.exists()) {
            file.delete();
        } else {
            throw new NotFoundException();
        }
    }

    public Entry<String, Item>[] query(String queryString) {
        throw new UnsupportedOperationException();
    }

}
