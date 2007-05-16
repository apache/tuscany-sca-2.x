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

package org.apache.tuscany.sca.core;


/**
 * The registry for the Tuscany core extension points.
 * 
 * @version $Rev$ $Date$
 */
public interface ExtensionPointRegistry {
    
    /**
     * Add an extension point to the registry
     * @param extensionPoint The instance of the extension point
     */
    void addExtensionPoint(Object extensionPoint);
    
    /**
     * Get the extension point by the interface
     * @param <T>
     * @param extensionPointType
     * @return
     */
    <T> T getExtensionPoint(Class<T> extensionPointType);
    
    /**
     * Remove an extension point
     * @param extensionPoint
     */
    void removeExtensionPoint(Object extensionPoint);
}
