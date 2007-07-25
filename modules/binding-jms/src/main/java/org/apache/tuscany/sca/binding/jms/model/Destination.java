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
 * Identifies the destination that is to be used to process requests/responses by this 
 * binding.
 * 
 * @version $Rev$ $Date$
 */
public interface Destination extends PropertyList {
    /**
     * The name of the destination to which the binding is connected. This may be a JNDI 
     * name or a plain destination name
     * 
     * @return
     */
    String getName();

    void setName(String name);

    DestinationType getType();

    void setType(DestinationType type);

    CreateResource getCreate();

    void setCreate(CreateResource create);
}
