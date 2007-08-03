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

package org.apache.tuscany.sca.contribution.namespace.impl;

import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.namespace.NamespaceExport;
import org.apache.tuscany.sca.contribution.namespace.NamespaceImport;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;

/**
 * The representation of an import for the contribution
 * 
 * @version $Rev$ $Date$
 */
public class NamespaceImportImpl implements NamespaceImport {
    private ModelResolver modelResolver;
    /**
     * The namespace to be imported
     */
    private String namespace; 
    /**
     * Optional location URI pointing to a Contribution that exports the namespace
     */
    private String location;

    
    protected NamespaceImportImpl() {
        super();
    }
    
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    
    public ModelResolver getModelResolver() {
        return modelResolver;
    }
    
    public void setModelResolver(ModelResolver modelResolver) {
        this.modelResolver = modelResolver;
    }

    /**
     * Match a NamespaceImport to a given NamespaceExport based on :
     *    location is not provided
     *    import and export namespaces match
     */
    public boolean match(Export export) {
        if (export instanceof NamespaceExport) {
            if (this.getLocation() == null || this.getLocation().length() == 0) {
                if (this.getNamespace().equals(((NamespaceExport)export).getNamespace())) {
                    return true;
                }
            }
            
        }
        return false;
    }
}
