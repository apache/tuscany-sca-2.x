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

package org.apache.tuscany.sca.binding.atom.news;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.NotFoundException;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Service;

@Service(NewsService.class) 
public class NewsServiceImpl implements NewsService {
    private Map<String, Headline> headlines = new HashMap<String,Headline>();

    @Init
    public void init() {
        Headline headline;

        headline = new Headline();
        headline.setSoure("http://www.domain.com/source1");
        headline.setText("headline text 1111");
        
        headlines.put("h1", headline);


        headline = new Headline();
        headline.setSoure("http://www.domain.com/source2");
        headline.setText("headline text 2222");
        
        headlines.put("h2", headline);
    }

    public Entry<String, Headline>[] getAll() {
        Entry<String, Headline>[] entries = new Entry[headlines.size()];
        int i = 0;
        for (Map.Entry<String, Headline> e: headlines.entrySet()) {
            entries[i++] = new Entry<String, Headline>(e.getKey(), e.getValue());
        }
        return entries;
    }

    public Headline get(String key) throws NotFoundException {
        Headline item = headlines.get(key);
        if (item == null) {
            throw new NotFoundException(key);
        } else {
            return item;
        }
    }

    public String post(String key, Headline item) {
        // TODO Auto-generated method stub
        return null;
    }

    public void put(String key, Headline item) throws NotFoundException {
        // TODO Auto-generated method stub

    }

    public Entry<String, Headline>[] query(String queryString) {
        // TODO Auto-generated method stub
        return null;
    } 

    public void delete(String key) throws NotFoundException {
        // TODO Auto-generated method stub

    }

}  
