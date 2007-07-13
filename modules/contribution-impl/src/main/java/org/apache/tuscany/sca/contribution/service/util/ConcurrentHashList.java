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

package org.apache.tuscany.sca.contribution.service.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashList <K,T> {
    private Map<K, List<T>> hashList;

    public ConcurrentHashList() {
        hashList = new  ConcurrentHashMap<K, List<T>>();
    }

    public ConcurrentHashList(int initialCapacity) {
        hashList = new  ConcurrentHashMap<K, List<T>>(initialCapacity);
    }
    
    public void clear() {
        hashList.clear();
    }

    public List<T> get(K key) {
        List<T> resultList = hashList.get(key);
        if( resultList == null) {
            resultList = new ArrayList<T>();
            hashList.put(key, resultList);
        }
        return resultList;
    }

    public boolean isEmpty() {
        return hashList.isEmpty();
    }

    public T put(K key, T value) {
        this.get(key).add(value);
        return value;
    }

    public int size() {
        return hashList.size();
    }


    public static void main(String args[]) {
        ConcurrentHashList<String, String> list = new ConcurrentHashList<String, String>();
        list.put("a", "a1");
        list.put("a", "a2");
        list.put("a", "a3");
        
        for(String s : list.get("a")) {
            System.out.println("Key a - " + s);
        }
    }
    
}
