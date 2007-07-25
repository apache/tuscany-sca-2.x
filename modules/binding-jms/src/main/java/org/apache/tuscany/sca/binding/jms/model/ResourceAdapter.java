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

package org.apache.tuscany.sca.binding.jms.model;

/**
 * Specifies name, type and properties of the Resource Adapter Java bean. This is required 
 * when the JMS resources are to be created for a JCA 1.5-compliant JMS provider, and is 
 * ignored otherwise. There may be a restriction, depending on the deployment platform, 
 * about specifying properties of the RA Java Bean. For non-JCA 1.5-compliant JMS providers, 
 * information necessary for resource creation must be done in provider-specific elements or 
 * attributes allowed by the extensibility of the binding.jms element.
 * 
 * @version $Rev$ $Date$
 */
public interface ResourceAdapter extends PropertyList {
    String getName();

    void setName(String name);
}
