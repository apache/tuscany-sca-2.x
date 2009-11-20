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

package org.apache.tuscany.sca.invocation;

/**
 * An invoker or interceptor can optionally implement this interface to indicate if they can 
 * enforce the pass-by-value semantics for an operation on remotable interfaces.
 * 
 * @version $Rev$ $Date$
 */
public interface DataExchangeSemantics {
    /**
     * Indicate if the data can be passed in by reference as they won't be mutated.
     * @return true if pass-by-reference is allowed
     */
    boolean allowsPassByReference();
}
