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
package org.apache.tuscany.spi.storer;

import org.apache.tuscany.spi.model.ModelObject;

/**
 * A registry for storers against model object types.
 * 
 * @version $Rev$ $Date$
 *
 */
public interface StorerRegistry extends Storer {
    
    /**
     * Registers a model object storer for a given type.
     * @param <T> Model object type.
     * @param modelObjectType Class of the model object type.
     * @param storer Storer for the model object type.
     */
    <T extends ModelObject> void register(Class<T> modelObjectType, Storer<T> storer);
    
    /**
     * Unregisters a model object storer for a given type.
     * @param <T> Model object type.
     * @param modelObjectType Class of the model object type.
     * @throws If no storer found for the specified type.
     */
    <T extends ModelObject> void unRegister(Class<T> modelObjectType) throws StorerException;

}
