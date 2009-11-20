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

package org.apache.tuscany.sca.contribution.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.impl.ExtensibleImpl;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;

/**
 * The representation of a deployed contribution
 *
 * @version $Rev$ $Date$
 */
class ContributionImpl extends ExtensibleImpl implements Contribution {
    private String uri;
    private String location;
    private Object model;
    private byte[] contents;
    private List<Export> exports = new ArrayList<Export>();
    private List<Import> imports = new ArrayList<Import>();
    private List<Composite> deployables = new ArrayList<Composite>();
    private List<Artifact> artifacts = new ArrayList<Artifact>();
    private List<Contribution> dependencies = new ArrayList<Contribution>();
    private ModelResolver modelResolver;
    private Set<String> types = new HashSet<String>();

    // FIXME remove this dependency on Java ClassLoaders
    private ClassLoader classLoader;

    ContributionImpl() {
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    //FIXME Remove dependency on Java ClassLoaders
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    //FIXME Remove dependency on Java ClassLoaders
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }


    public String getURI() {
        return this.uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public <T> T getModel() {
        return (T) model;
    }

    public void setModel(Object model) {
        this.model = model;
    }

    public byte[] getContents() {
        return contents;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }

    public ModelResolver getModelResolver() {
        return modelResolver;
    }

    public void setModelResolver(ModelResolver modelResolver) {
        this.modelResolver = modelResolver;
    }

    public List<Contribution> getDependencies() {
        return dependencies;
    }

    public List<Export> getExports() {
        return exports;
    }

    public List<Import> getImports() {
        return imports;
    }

    public List<Composite> getDeployables() {
        return deployables;
    }

    public List<Artifact> getArtifacts() {
        return artifacts;
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            if (obj instanceof Artifact) {
                return uri.equals(((Artifact)obj).getURI());
            } else {
                return false;
            }
        }
    }

    @Override
    public String toString() {
    	return "Contribution : " + uri + " \n" +
    	       "from: " + location;
    }

    public Set<String> getTypes() {
        return types;
    }

}
