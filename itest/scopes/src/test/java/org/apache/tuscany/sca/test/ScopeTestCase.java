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

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.itest.scopes.StateVerifier;

public class ScopeTestCase extends TestCase {

    final static int numThreads = 4; // number of threads to drive each scope container
    final static int iterations = 200; // number of iterations per thread
    private SCADomain domain;

    // Test scope containers.
    // The request scope container isn't hooked up for some reason so the code below
    // that tests request scope is commented out.
    // Code could be added to test session scope once it is supported in a standalone environment.

    public void testScopes() throws InterruptedException {

      Thread[] moduleScopeThreadTable = new Thread[numThreads];
      Thread[] requestScopeThreadTable = new Thread[numThreads];

      for(int i=0; i<numThreads; i++)
      {
        moduleScopeThreadTable[i] = new ModuleScopeTestThread();
        requestScopeThreadTable[i] = new RequestScopeTestThread();
      }
      for(int j=0; j<numThreads; j++)
      {
        moduleScopeThreadTable[j].start();
        requestScopeThreadTable[j].start();
      }
      for(int k=0; k<numThreads; k++)
      {
        moduleScopeThreadTable[k].join();
        requestScopeThreadTable[k].join();
      }
    }

    private  class ModuleScopeTestThread extends Thread {
      public void run() {
        StateVerifier moduleScopeService = domain.getService(StateVerifier.class, "ModuleScopeComponent");
        for(int i=1; i<=iterations; i++) {
          moduleScopeService.setState(i);
          if (!moduleScopeService.checkState(i))
            fail("The module scope service lost its state on iteration " + i);
        }
      }
    }

    private class RequestScopeTestThread extends Thread {
      public void run() {
        StateVerifier requestScopeService = domain.getService(StateVerifier.class, "RequestScopeComponent");
        for(int i=1; i<=iterations; i++) {
          requestScopeService.setState(i);
          if (!requestScopeService.checkState(i))
            fail("The request scope service lost its state on iteration " + i);
        }
      }
    }

    protected void setUp() throws Exception {
        domain = SCADomain.newInstance("scopes.composite");
    }

    protected void tearDown() throws Exception {
        if (domain != null) {
            domain.close();
        }
    }
    
}
