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

package services;

import org.apache.tuscany.sca.binding.feed.collection.Collection;
import org.apache.tuscany.sca.binding.feed.collection.NotFoundException;
import org.osoa.sca.annotations.Reference;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;

public class ShoppingCartImpl implements Collection {

    @Reference
    public Collection shoppingCart;

    public Feed getFeed() {
        return shoppingCart.getFeed();
    }

    public Entry get(String id) throws NotFoundException {
        return shoppingCart.get(id);
    }

    public Entry post(Entry entry) {
        return shoppingCart.post(entry);
    }

    public Entry put(String id, Entry entry) throws NotFoundException {
        return shoppingCart.put(id, entry);
    }

    public void delete(String id) throws NotFoundException {
        shoppingCart.delete(id);
    }

}
