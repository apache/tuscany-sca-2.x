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

package org.apache.tuscany.sca.contribution.resource.impl;

import java.util.List;

import org.apache.tuscany.sca.assembly.impl.ExtensibleImpl;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resource.ResourceExport;
import org.apache.tuscany.sca.contribution.resource.ResourceImport;

/**
 * The representation of an import for the contribution
 *
 * @version $Rev$ $Date$
 */
public class ResourceImportImpl extends ExtensibleImpl implements ResourceImport {
    /**
     * The resource URI to be imported
     */
    private String uri;

    private ModelResolver modelResolver;
    private List<Contribution> exportContributions;

    /**
     * Optional location URI pointing to a Contribution that exports the resource
     */
    private String location;

    protected ResourceImportImpl() {
        super();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public ModelResolver getModelResolver() {
        return modelResolver;
    }

    public void setModelResolver(ModelResolver modelResolver) {
        this.modelResolver = modelResolver;
    }

    public List<Contribution> getExportContributions() {
        return exportContributions;
    }

    public void setExportContributions(List<Contribution> contributions) {
        this.exportContributions = contributions;
    }

    /**
     * Match a ResourceImport to a given ResourceExport based on :
     *    location is not provided
     *    import and export resource URI match
     */
    public boolean match(Export export) {
        if (export instanceof ResourceExport) {
            if (this.getLocation() == null || this.getLocation().length() == 0) {
                if (this.getURI().equals(((ResourceExport)export).getURI())) {
                    return true;
                }
            }

        }
        return false;
    }

    @Override
    public String toString() {
        return String.valueOf(uri);
    }
}
