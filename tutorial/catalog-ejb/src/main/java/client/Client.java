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

import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import services.ejb.CatalogEJBHome;
import services.ejb.CatalogEJBRemote;
import services.ejb.Vegetable;

/**
 * A test client for the catalog EJB. 
 *
 * @version $Rev: $ $Date: $
 */
public class Client {

    public static void main(String[] args) throws Exception {
        InitialContext context = new InitialContext();
        
        Object o = context.lookup("corbaname:iiop:1.2@localhost:1050#VegetablesCatalogEJB");
        CatalogEJBHome home = (CatalogEJBHome) PortableRemoteObject.narrow(o, CatalogEJBHome.class);
        CatalogEJBRemote catalog = home.create();
    
        Vegetable items[] = catalog.get();
        for (Vegetable item: items) {
            System.out.println(item.getName() + " " + item.getPrice());
        }
    }

}
