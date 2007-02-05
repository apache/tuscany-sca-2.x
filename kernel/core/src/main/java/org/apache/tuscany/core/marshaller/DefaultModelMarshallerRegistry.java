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
package org.apache.tuscany.core.marshaller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.spi.marshaller.ModelMarshaller;
import org.apache.tuscany.spi.marshaller.ModelMarshallerRegistry;
import org.apache.tuscany.spi.model.physical.PhysicalComponentDefinition;

/**
 * Default map-based implementation of the model marshaller registry. 
 * 
 * TODO may be we can factor out all the registries into a parameterized one.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultModelMarshallerRegistry<P extends PhysicalComponentDefinition> implements ModelMarshallerRegistry {

    // Internal cache
    private Map<Class<? extends PhysicalComponentDefinition>, ModelMarshaller<? extends PhysicalComponentDefinition>> registry =
        new ConcurrentHashMap<Class<? extends PhysicalComponentDefinition>, ModelMarshaller<? extends PhysicalComponentDefinition>>();

    /**
     * Registers a physical component definition marshaller.
     * 
     * @param <PCD> Physical component definition.
     * @param modelClass Physical component definition class.
     * @param marshaller Marshaller responsible for marshalling.
     */
    public <PCD extends PhysicalComponentDefinition> void registerMarshaller(Class<PCD> modelClass,
                                                                             ModelMarshaller<PCD> marshaller) {
        registry.put(modelClass, marshaller);
    }

    /**
     * Gets a marshaller for marshalling the registered type.
     * 
     * @param <PCD> Physical component definition.
     * @param modelClass Type of the physical model definition that needs to be
     *            marshalled.
     * @return Marshaller capable for marshalling the specified type.
     */
    @SuppressWarnings("unchecked")
    public <PCD extends PhysicalComponentDefinition> ModelMarshaller<PCD> getMarshaller(Class<PCD> modelClass) {
        return (ModelMarshaller<PCD>)registry.get(modelClass);
    }

}
