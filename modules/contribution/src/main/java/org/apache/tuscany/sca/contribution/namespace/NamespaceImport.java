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

package org.apache.tuscany.sca.contribution.namespace;

import org.apache.tuscany.sca.contribution.Import;

/**
 * The representation of an XML namespace import.
 * 
 * @version $Rev$ $Date$
 */
public interface NamespaceImport extends Import {

    /**
     * Get the location used to resolve the definitions for this import
     * 
     * @return The import location
     */
    String getLocation();

    /**
     * Set the location used to resolve the definitions for this import
     * 
     * @param location The import location
     */
    void setLocation(String location);

    /**
     * Get Namespace that identifies the import
     * 
     * @return The namespace
     */
    String getNamespace();

    /**
     * Set Namespace that identifies the import
     * 
     * @param namespace The namespace
     */
    void setNamespace(String namespace);
    
}