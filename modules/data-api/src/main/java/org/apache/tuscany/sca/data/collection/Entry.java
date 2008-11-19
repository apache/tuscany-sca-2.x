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
package org.apache.tuscany.sca.data.collection;


/**
 * Represents a key/data pair in a data collection.
 * 
 * @version $Rev$ $Date$
 */
public class Entry <K, D> {

    private K key;
    private D data;
    
    /**
     * Constructs a new entry.
     */
    public Entry() {
    }
    
    /**
     * Constructs a new entry.
     * @param key the entry key
     * @param data the entry data
     */
    public Entry(K key, D data) {
        this.key = key;
        this.data = data;
    }
    
    /**
     * Returns the entry key.
     * @return the key
     */
    public K getKey() {
        return key;
    }
    
    /**
     * Sets the entry key.
     * @param key the key
     */
    public void setKey(K key) {
        this.key = key;
    }
    
    /**
     * Returns the entry data.
     * @return the entry data
     */
    public D getData() {
        return data;
    }
    
    /**
     * Sets the entry data
     * @param data the entry data
     */
    public void setData(D data) {
        this.data = data;
    }

    //FIXME Temporary methods to make JAXB register the Item
    // class when the Entry class is registered in a JAXB context  
    public void setDummy(Item item) {}
    public Item getDummy() { return null; }
    
}
