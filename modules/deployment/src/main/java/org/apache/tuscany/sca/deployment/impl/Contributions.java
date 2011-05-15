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

package org.apache.tuscany.sca.deployment.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Extension;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionMetadata;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;

/**
 * A Contribution impl wrapping multiple other contributions
 * Currently the sole reason for this is so
 */
public class Contributions implements Contribution {

    private List<Contribution> contributions = new ArrayList<Contribution>();
    private String location;
    private String uri;
    private Object model;
    private byte[] contents;
    private boolean unresolved;
    private ModelResolver modelResolver;
    private List<Contribution> dependencies = new ArrayList<Contribution>();

    public Contributions(List<Contribution> contributions) {
        this.contributions = contributions;
    }

    public String getLocation() {
        return location;
    }

    public Object getModel() {
        return model;
    }

    public String getURI() {
        return uri;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public void setURI(String uri) {
        this.uri = uri;
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }

    public List<Contribution> getContributions() {
        return contributions;
    }

    public List<Artifact> getArtifacts() {
        return (List<Artifact>)(Object)contributions;
    }

    public List<Contribution> getDependencies() {
        return dependencies;
    }

    public ClassLoader getClassLoader() {
        //FIXME Remove later
        return null;
    }

    public void setClassLoader(ClassLoader classLoader) {
        //FIXME Remove later
    }

    public List<Composite> getDeployables() {
        List<Composite> deployables = new ArrayList<Composite>();
        for (Contribution contribution: contributions) {
            deployables.addAll(contribution.getDeployables());
        }
        return deployables;
    }

    public List<Export> getExports() {
        List<Export> exports = new ArrayList<Export>();
        for (Contribution contribution: contributions) {
            exports.addAll(contribution.getExports());
        }
        return exports;
    }

    public List<Import> getImports() {
        List<Import> imports = new ArrayList<Import>();
        for (Contribution contribution: contributions) {
            imports.addAll(contribution.getImports());
        }
        return imports;
    }

    public ModelResolver getModelResolver() {
        return modelResolver;
    }

    public void setModelResolver(ModelResolver modelResolver) {
        this.modelResolver = modelResolver;
    }

    public List<Extension> getAttributeExtensions() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Object> getExtensions() {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<String> getTypes() {
        return Collections.emptySet();
    }

    @Override
    public <T> T getArtifactModel(String URI) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void mergeMetaData(ContributionMetadata metaData) {
        // TODO Auto-generated method stub
    }

    @Override
    public void addComposite(Composite composite) {
        // TODO Auto-generated method stub
        
    }
}
