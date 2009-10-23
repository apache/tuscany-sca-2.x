package itest;
import java.util.concurrent.ExecutionException;

import junit.framework.Assert;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import stock.StockQuoteClient;

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

public class AsyncServiceTestCase {

    private Node node;

    @Before
    public void init() {
        node = NodeFactory.newInstance().createNode().start();
    }

    @After
    public void end() {
        if (node != null) {
            node.stop();
        }
    }

    @Test
    public void invokeRPC() {
        StockQuoteClient sc = node.getService(StockQuoteClient.class, "StockQuoteClient");
        Assert.assertEquals(10.0f, sc.getPrice("foo"));
    }
    
    @Test
    public void invokeAsyncPoll() throws InterruptedException, ExecutionException {
        StockQuoteClient sc = node.getService(StockQuoteClient.class, "StockQuoteClient");
        Assert.assertEquals(10.0f, sc.getPriceAsyncPoll("foo"));
    }

//    @Test
//    public void invokeAsyncCallback() throws Exception {
//        StockQuoteClient sc = node.getService(StockQuoteClient.class, "StockQuoteClient");
//        Assert.assertEquals(10.0f, sc.getPriceAsyncCallback("foo"));
//    }

}
