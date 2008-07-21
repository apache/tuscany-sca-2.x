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

import org.junit.Ignore;
import org.junit.Test;

import services.ejb.CatalogEJBHome;
import services.ejb.CatalogEJBRemote;
import services.ejb.Vegetable;

/**
 * A test client for the catalog EJB. 
 *
 * @version $Rev$ $Date$
 */
public class CatalogEJBClientTestCase {

    @Test
    @Ignore // Ignore the test case for maven build, remove it if you want to run
    public void testCatalogEJB() throws Exception {
        InitialContext context = new InitialContext();

        Object o = context.lookup("corbaname:iiop:1.2@localhost:1050#VegetablesCatalogEJB");

        // The narrow(...) call requires generated EJB stubs. Tuscany binding.ejb doesn't the stubs
        CatalogEJBHome home = (CatalogEJBHome)PortableRemoteObject.narrow(o, CatalogEJBHome.class);

        // The following call will hang with SUN jdk1.6.0_05, please use SUN or IBM jdk 1.5.x instead
        CatalogEJBRemote catalog = home.create();

        Vegetable items[] = catalog.get();
        for (Vegetable item : items) {
            System.out.println(item.getName() + " " + item.getPrice());
        }
    }

    public static void main(String args[]) throws Exception {
        String javaVersion = System.getProperty("java.version");
        String javaVendor = System.getProperty("java.vendor");

        if (javaVendor.toUpperCase().contains("SUN") && javaVersion.startsWith("1.6.")) {
            System.err.println("The EJB invocation may hang due to a bug in " + javaVendor + ":" + javaVersion);
        }
        new CatalogEJBClientTestCase().testCatalogEJB();
    }

}
