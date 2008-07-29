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

package client;

import org.apache.tuscany.sca.data.collection.NotFoundException;
import org.osoa.sca.annotations.Reference;

import services.Cart;
import services.Catalog;
import services.Item;
import services.Total;

public class ShopperImpl implements Shopper {
    
    @Reference
    public Catalog catalog;
    
    @Reference
    public Cart shoppingCart;
    
    @Reference
    public Total shoppingTotal;

    public String shop(String itemName, int quantity) {
        
        Item[] items = catalog.get();
        for (Item item: items) {
            if (item.getName().startsWith(itemName)) {
                
                try {
                    shoppingCart.delete("");
                } catch (NotFoundException e) {
                }

                for (int i = 0; i < quantity; i++) {
                    shoppingCart.post("item" + i, item);
                }
                
                return shoppingTotal.getTotal();
            }
        }
        
        return "";
    }

}
