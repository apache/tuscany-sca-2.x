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
package org.apache.tuscany.sca.implementation.node;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Implementation;



/**
 * The model representing a resource implementation in an SCA assembly model.
 *
 * @version $Rev$ $Date$
 */
public interface NodeImplementation extends Implementation {
    
    /**
     * Returns the composite deployed to this node.
     * @return the composite deployed to this node
     */
    Composite getComposite();
    
    /**
     * Sets the composite deployed to this node.
     * @param composite the composite deployed to this node
     */
    void setComposite(Composite composite);

}
