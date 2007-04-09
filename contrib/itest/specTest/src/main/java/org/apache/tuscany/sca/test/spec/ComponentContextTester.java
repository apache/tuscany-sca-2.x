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
package org.apache.tuscany.sca.test.spec;

/**
 * @version $Rev$ $Date$
 */
public interface ComponentContextTester extends IdentityService {
    /**
     * Returns true if the ComponentContext was injected
     * @return true if the ComponentContext was injected
     */
    boolean isContextInjected();

    /**
     * Looks up a reference with the supplied name and returns the identity of the referenced component.
     * @param name the name of a reference
     * @return the identity of the referenced component
     */
    String getServiceIdentity(String name);

    /**
     * Looks up a reference with the supplied name using a ServiceReference
     * and returns the identity of the referenced component.
     * @param name the name of a reference
     * @return the identity of the referenced component
     */
    String getServiceReferenceIdentity(String name);
}
