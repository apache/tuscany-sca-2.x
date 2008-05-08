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

package test;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCANode2;
import org.apache.tuscany.sca.node.launcher.DomainManagerLauncher;
import org.apache.tuscany.sca.node.launcher.NodeLauncher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import client.Shopper;


/**
 * Test the store-merger.
 *
 * @version $Rev$ $Date$
 */
public class StoreSupplierTestCase {
    
    private SCANode2 domainManager;
    private SCANode2 storeSupplierNode;
    private SCANode2 storeCatalogsNode;
    private SCANode2 storeClientNode;

    @Before
    public void setup() throws Exception {
        String baseDir = System.getProperty("basedir");
        String domainDir = baseDir != null? baseDir + "/" + "../domain" : "../domain";
        
        DomainManagerLauncher managerLauncher = DomainManagerLauncher.newInstance();
        domainManager = managerLauncher.createDomainManager(domainDir);
        domainManager.start();
        
        NodeLauncher nodeLauncher = NodeLauncher.newInstance();
        storeSupplierNode = nodeLauncher.createNode("http://localhost:9990/node-config/StoreSupplierNode");
        storeSupplierNode.start();
        
        storeCatalogsNode = nodeLauncher.createNode("http://localhost:9990/node-config/CatalogsNode");
        storeCatalogsNode.start();
        
        storeClientNode = nodeLauncher.createNode("http://localhost:9990/node-config/StoreClientNode");
        storeClientNode.start();
        
    }
    
    @After
    public void tearDown() throws Exception {
        storeSupplierNode.stop();
        storeCatalogsNode.stop();
        storeClientNode.stop();
        domainManager.stop();
    }
    
    @Test
    public void testShop() {
        SCAClient client = (SCAClient)storeClientNode;
        Shopper shopper = client.getService(Shopper.class, "StoreClient");
        
        String total = shopper.shop("Orange", 5);
        System.out.println("Total: " + total);
        
        Assert.assertEquals("$17.75", total);
        
    }
    
}
