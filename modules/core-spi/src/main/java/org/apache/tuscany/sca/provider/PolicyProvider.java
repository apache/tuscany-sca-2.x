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

package org.apache.tuscany.sca.provider;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.PhasedInterceptor;

/**
 * @version $Rev$ $Date$
 */
public interface PolicyProvider extends RuntimeProvider {
    /**
     * Create an interceptor for a given operation
     * 
     * @param operation
     * @return An interceptor that realizes the policySet
     */
    PhasedInterceptor createInterceptor(Operation operation);
    
    /**
     * Create a binding interceptor. The binding wire is 
     * not operation specific so an operation parameter 
     * isn't required
     * 
     * @return An interceptor that realizes the policySet
     */
    PhasedInterceptor createBindingInterceptor();
    
    /**
     * Give the provider an opportunity to affect the 
     * binding configuration if required
     * 
     * @param configurationContext the configuration context of the 
     *        binding that will be modified
     */
    void configureBinding(Object configuration);
}
