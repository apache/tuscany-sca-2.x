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

package services.mediation;

import java.rmi.RemoteException;

import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.annotations.Reference;

import services.Catalog;
import services.Item;
import services.ejb.CatalogEJBRemote;
import services.ejb.Vegetable;

public class VegetablesCatalogMediationImpl implements Catalog {

    @Reference
    public CatalogEJBRemote catalog;
    
    public Item[] get() {
        Vegetable[] vegetables;
        try {
            vegetables = catalog.get();
        } catch (RemoteException e) {
            throw new ServiceRuntimeException(e);
        }
        Item[] items = new Item[vegetables.length];
        for (int i = 0; i < vegetables.length; i++) {
            items[i] = new Item(vegetables[i].getName(), vegetables[i].getPrice());
        }
        return items;
    }
    
}
