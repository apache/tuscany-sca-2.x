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
package org.apache.tuscany.sca.api;

import java.util.Collection;

import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;

/**
 * An extension of the OSOA ComponentContext that provides access to collections
 * of references. 
 * 
 * @version $Rev$ $Date$
 */
public interface ComponentContextExtension extends ComponentContext {
    
   
    /* ******************** Contribution for issue TUSCANY-2281 ******************** */

    /**
     * Returns a Collection of typed service proxies for a business interface type and a reference name.
     * @param businessInterface the interface that will be used to invoke the service
     * @param referenceName the name of the reference
     * @param <B> the Java type of the business interface for the reference
     * @return a Collection of objects that implements the business interface 
     */
    <B> Collection<B> getServices(Class<B> businessInterface, String referenceName); 
 

    /**
     * Returns a Collection of typed service reference for a business interface type and a reference name. 
     * @param businessInterface the interface that will be used to invoke the service
     * @param referenceName the name of the reference
     * @param <B> the Java type of the business interface for the reference
     * @return a Collection of objects that implements the business interface
     */
    <B> Collection<ServiceReference<B>> getServiceReferences(Class<B> businessInterface, String referenceName); 
}
