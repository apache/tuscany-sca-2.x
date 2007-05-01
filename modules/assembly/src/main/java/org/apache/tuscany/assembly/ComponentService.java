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
package org.apache.tuscany.assembly;

import java.util.List;

/**
 * An addressable instance of a service associated with a particular component.
 * 
 * @version $Rev$ $Date$
 */
public interface ComponentService extends Service {

    /**
     * Returns the service defined by the implementation for this service.
     * 
     * @return
     */
    Service getService();

    /**
     * Sets the service defined by the implementation for this service.
     * 
     * @param service
     */
    void setService(Service service);

    /**
     * Derived relationship. Returns the list of composite services that
     * promote this service.
     *  
     * @return
     */
    List<CompositeService> promotedAs();
    
    /**
     * Clone the component service.
     * 
     * @return a clone of the component service
     */
    Object clone() throws CloneNotSupportedException;
}
