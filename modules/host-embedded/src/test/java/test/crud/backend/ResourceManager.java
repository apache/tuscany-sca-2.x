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

package test.crud.backend;

import java.util.HashMap;
import java.util.Map;

/**
 * A fake resource manager implementation used as a backend by the sample
 * CRUD component implementation.
 *  
 * @version $Rev$ $Date$
 */
public class ResourceManager {
    private static int counter;
    private static final Map<String, Object> store = new HashMap<String, Object>();
    private String directory;

    /**
     * Constructs a new resource manager.
     * 
     * @param directory the directory where to persist resources
     */
    public ResourceManager(String directory) {
        super();
        this.directory = directory;
    }

    /**
     * Creates a new resource.
     * 
     * @param resource
     * @return
     */
    public String createResource(Object resource) {
        System.out.println("create(" + resource + ") in " + directory);
        String key = String.valueOf(counter++);
        store.put(key, resource);
        return key;
    }

    /**
     * Deletes a resource.
     * 
     * @param id
     */
    public void deleteResource(String id) {
        System.out.println("delete(" + id + ")");
        store.remove(id);
    }

    /**
     * Retrieves a resource.
     * 
     * @param id
     * @return
     */
    public Object retrieveResource(String id) {
        System.out.println("retrieve(" + id + ")");
        return store.get(id);
    }

    /**
     * Updates a resource.
     * 
     * @param id
     * @param resource
     * @return
     */
    public Object updateResource(String id, Object resource) {
        System.out.println("update(" + id + ")");
        return store.put(id, resource);
    }

}
