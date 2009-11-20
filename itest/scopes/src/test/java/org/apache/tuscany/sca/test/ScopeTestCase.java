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
package org.apache.tuscany.sca.test;

import static org.junit.Assert.fail;

import org.apache.tuscany.sca.itest.scopes.StateVerifier;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ScopeTestCase {

    /**
     * Number of threads to drive each scope container.
     */
    private static final int NUM_THREADS = 4;
    
    /**
     *  Number of iterations per thread.
     */
    private static final int ITERATIONS = 200;
    
    private Node node;

    @Before
    public void setUp() throws Exception {
        String location = ContributionLocationHelper.getContributionLocation("scopes.composite");
        node = NodeFactory.newInstance().createNode("scopes.composite", new Contribution("c1", location));
        node.start();
    }

    @After
    public void tearDown() throws Exception {
        node.stop();
    }

    @Test
    public void testScopes() throws InterruptedException {

        Thread[] moduleScopeThreadTable = new Thread[NUM_THREADS];

        for (int i = 0; i < NUM_THREADS; i++) {
            moduleScopeThreadTable[i] = new ModuleScopeTestThread();
        }
        for (int j = 0; j < NUM_THREADS; j++) {
            moduleScopeThreadTable[j].start();
        }
        for (int k = 0; k < NUM_THREADS; k++) {
            moduleScopeThreadTable[k].join();
        }
    }

    private class ModuleScopeTestThread extends Thread {

        public void run() {
            StateVerifier moduleScopeService = node.getService(StateVerifier.class, "ModuleScopeComponent");
            for (int i = 1; i <= ITERATIONS; i++) {
                moduleScopeService.setState(i);
                if (!moduleScopeService.checkState(i)) {
                    fail("The module scope service lost its state on iteration " + i);
                }
            }
        }
    }



}
