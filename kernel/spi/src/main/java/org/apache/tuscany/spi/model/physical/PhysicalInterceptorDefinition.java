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
package org.apache.tuscany.spi.model.physical;

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.model.ModelObject;

/**
 * Model class representing the portable definition of an interceptor. This class 
 * is used to describe the interceptors around inbound and outbound wires on a 
 * physical component definition.
 * 
 * @version $Rev$ $Date$
 *
 */
public class PhysicalInterceptorDefinition extends ModelObject {
    
    // The qualified name of the interceptor builder
    private QName builder;

    public PhysicalInterceptorDefinition(QName builder) {
        this.builder = builder;
    }

    /**
     * Gets the qualified name of the builder.
     * @return Qualified name of the builder.
     */
    public QName getBuilder() {
        return builder;
    }

    /**
     * Sets the qualified name of the builder.
     * @param builder Qualified name of the builder.
     */
    public void setBuilder(QName builder) {
        this.builder = builder;
    }

}
