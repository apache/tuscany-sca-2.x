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
package org.apache.tuscany.core.model.physical.java;

import org.apache.tuscany.spi.model.physical.PhysicalComponentDefinition;

/**
 * Represents the physical component definition for a Java implementation.
 *
 * @version $Rev$ $Date$
 */
public class JavaPhysicalComponentDefinition extends PhysicalComponentDefinition<JavaPhysicalServiceDefinition, JavaPhysicalReferenceDefinition> {

    // The byte code for the instance factory
    private byte[] instanceFactoryByteCode;

    /**
     * Gets the byte code for the instance factory.
     *
     * @return Byte code for the instance factory.
     */
    public byte[] getInstanceFactoryByteCode() {
        return instanceFactoryByteCode;
    }

    /**
     * Sets the byte code for the instance factory.
     *
     * @param instanceFactoryByteCode Byte code for the instance factory.
     */
    public void setInstanceFactoryByteCode(byte[] instanceFactoryByteCode) {

        if (instanceFactoryByteCode == null) {
            throw new IllegalArgumentException("Instance factory byte code is null");
        }
        this.instanceFactoryByteCode = instanceFactoryByteCode;

    }

}
