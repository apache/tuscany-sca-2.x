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

package org.apache.tuscany.sca.endpointresolver;


/**
 * A reference binding implementation can options implement this
 * interface to tie into the Tuscany SCA runtime
 * 
 * @version $Rev$ $Date$
 */
public interface EndpointResolver {

    /**
     * This method will be invoked when the endpoint is
     * activated. It gives the resolver the opportunity
     * to do any set up ready for when it is asked to 
     * resolve the endpoint when a message arrives
     */
    void start();
    
    /**
     * This method will be invoked when the endpoint is
     * to be resolved. The resolver will attempt to resolve the 
     * endpoint against available services. The resolvers extending
     * this interface will provide environment or binding specific 
     * resolution processing
     */
    void resolve();

    /**
     * This method will be invoked when the endpont is
     * deactivated. It gives the resolver the opportunity
     * to take and required resolver shutdown actions
     */
    void stop();    

}
