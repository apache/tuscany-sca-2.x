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

import java.net.URL;

import javax.xml.namespace.QName;

import launch.LaunchAmazonCart;

import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.apache.tuscany.sca.node.util.SCAContributionUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import amazon.cart.AmazonCart;

import com.cart.amazon.AmazonFactory;
import com.cart.amazon.CartCreate;
import commonj.sdo.DataObject;

/**
 * Test case for helloworld web service client
 */
public class AmazonCartTestCase {

    private SCANode node;
    private AmazonCart amazonCart;

    @Before
    public void startClient() throws Exception {
        try {

            System.out.println("Starting ...");
            SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
            node = nodeFactory.createSCANode(null, null);
            
            URL contribution = SCAContributionUtil.findContributionFromClass(LaunchAmazonCart.class);
            node.addContribution("http://amazonCart", contribution);
            
            node.addToDomainLevelComposite(new QName("http://amazonCart", "amazonCart"));
            node.start();

            System.out.println("amazoncart.composite ready for big business !!!");
            
            amazonCart = node.getDomain().getService(AmazonCart.class, "AmazonCartServiceComponent");

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCartCreate() throws Exception {
        System.out.println("Entering test...");
        CartCreate create = AmazonFactory.INSTANCE.createCartCreate();
        DataObject root = amazonCart.CartCreate(create);
        // Assert.assertEquals("Hello Smith", msg);
        System.out.println("Exiting test...");
    }

    /*
     * @Test public void testEmbeddedReferenceClient() throws Exception { String
     * msg = helloTuscanyService.getGreetings("Tuscany");
     * Assert.assertEquals("Hello Tuscany", msg); }
     */

    @After
    public void stopClient() throws Exception {
        System.out.println("Stopping ...");
        node.stop();
        node.destroy();
        System.out.println();
    }

    /*
     * public void testCartCreate() throws Exception { DataObject root =
     * amazonCart.cartCreate(null); //assertNotNull(root);
     * //assertEquals("Luciano Resende", root.getString("CUSTOMER[1]/NAME")); }
     */
}
