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

package store;

import java.io.IOException;
import java.net.MalformedURLException;

import junit.framework.Assert;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import client.Shopper;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


/**
 * Test the store-merger.
 *
 * @version $Rev$ $Date$
 */
public class StoreTestCase {
    private static Node nodeStore;

    @BeforeClass
    public static void setUp() throws Exception {
        String storeLocation = ContributionLocationHelper.getContributionLocation("store.composite");
        String storeClientLocation = ContributionLocationHelper.getContributionLocation("store-client.composite");

        nodeStore = NodeFactory.newInstance().createNode(new Contribution("store", storeLocation), new Contribution("storeClient", storeClientLocation));
        nodeStore.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        nodeStore.stop();
    }

    @Test
    @Ignore
    public void testWaitForInput() {
        try {
            System.out.println("press enter to continue)");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testShop() {
        Shopper shopper = nodeStore.getService(Shopper.class, "StoreClient");

        String total = shopper.shop("Orange", 5);
        System.out.println("Total: " + total);

        Assert.assertEquals("$17.75", total);

    }

    @Test
    @Ignore
    public void testStoreWidget() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
        WebClient browser = new WebClient(BrowserVersion.FIREFOX_2);
        browser.setRedirectEnabled(true);
        browser.setThrowExceptionOnScriptError(false);

        HtmlPage page = (HtmlPage) browser.getPage("http://localhost:8080/store/store.html");

        //delay to allow all javascript to be retrieved and loaded
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        HtmlForm form = (HtmlForm) page.getFormByName("catalogForm");


        HtmlCheckBoxInput catalogItems = (HtmlCheckBoxInput) form.getInputByName("items");

        System.out.println(">>>" + catalogItems.getAttributeValue("value"));
        Assert.assertEquals("Apple - $2.99", catalogItems.getAttributeValue("value"));

    }

}
