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

package crud;

import java.util.HashMap;
import java.util.Map;

/**
 * @version $Rev$ $Date$
 */
public class CRUDImpl implements CRUD {
    private static int counter;
    private static final Map<String, Object> STORE = new HashMap<String, Object>();

    private String directory;

    /**
     * @param directory
     */
    public CRUDImpl(String directory) {
        super();
        this.directory = directory;
    }

    public String create(Object resource) {
        System.out.println("create(" + resource + ")");
        String key = String.valueOf(counter++);
        STORE.put(key, resource);
        return key;
    }

    public void delete(String id) {
        System.out.println("delete(" + id + ")");
        STORE.remove(id);
    }

    public Object retrieve(String id) {
        System.out.println("retrieve(" + id + ")");
        return STORE.get(id);
    }

    public Object update(String id, Object resource) {
        System.out.println("update(" + id + ")");
        return STORE.put(id, resource);
    }

}
