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

package org.apache.tuscany.contribution;

import java.net.URI;

/**
 * The representation of an import for the contribution
 * 
 * @version $Rev$ $Date$
 */
public interface ContributionImport{
    // TODO: We might need the field to point to the imported artifact/model

    /**
     * 
     * @return
     */
    public URI getLocation();

    /**
     * @param location
     */
    public void setLocation(URI location);

    /**
     * Get Namespace that identifies the import
     * @return
     */
    public String getNamespace();

    /**
     * Set Namespace that identifies the import
     * @param namespace
     */
    public void setNamespace(String namespace);
}
