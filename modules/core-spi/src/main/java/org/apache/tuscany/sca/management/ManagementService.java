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
package org.apache.tuscany.sca.management;

import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * Interface for the management service abstraction. The implementaion
 * could be based on a variety of technologies including JMX, WSDM,
 * SNMP etc.
 *
 * @version $Revision$ $Date$
 */
public interface ManagementService {

    /**
     * Registers a component for management.
     *
     * @param name      Name of the component.
     * @param component Component to be registered.
     */
    void registerComponent(String name, RuntimeComponent component);
}
