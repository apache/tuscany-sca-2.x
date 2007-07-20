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
package org.apache.tuscany.sca.itest.scopes;

import org.osoa.sca.annotations.Scope;

// Test module scope
@Scope("COMPOSITE")
public class CompositeScopeStateVerifierImpl implements StateVerifier {

    // State data for this module (composite).
    // In order to support thread-based state verification,
    // the module state needs to be a ThreadLocal.
    ThreadLocal<Integer> moduleState;

    public CompositeScopeStateVerifierImpl() {
      moduleState = new ThreadLocal<Integer>();
    }

    public void setState(int i) {
      moduleState.set(i);
    }

    public boolean checkState(int i) {
      return (moduleState.get() == i);
    }

}
