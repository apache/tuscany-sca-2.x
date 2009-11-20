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

import java.util.List;

import javax.xml.namespace.QName;

/**
 * This interface can be optionally implemented by the Binding or Implementation providers to
 * indicate if they implement the policies in the binding/implementation provider.
 * 
 * @version $Rev$ $Date$
 */
public interface PolicyImplementor {
    /**
     * Get a list of policy names that are implemented by this policy implementor
     * @return A list of policy names
     */
    List<QName> getImplementedPolicies();
}
