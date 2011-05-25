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

package org.apache.tuscany.sca.runtime;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.java.JavaExport;
import org.apache.tuscany.sca.contribution.java.JavaImport;
import org.apache.tuscany.sca.contribution.namespace.NamespaceExport;
import org.apache.tuscany.sca.contribution.namespace.NamespaceImport;

public class ContributionDescription implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uri;
    private String url;
    private List<String> dependentContributionURIs = new ArrayList<String>();

    // the URIs of the deployable composites within the contribution
    private List<String> deployables = new ArrayList<String>();

    // TODO: Handle Imports and Exports in a more extensible way
    private List<String> javaExports = new ArrayList<String>();
    private List<String> namespaceExports = new ArrayList<String>();
    private List<String> javaImports = new ArrayList<String>();
    private List<String> namespaceImports = new ArrayList<String>();
    
    // the URI and XML content of composites to include in the contribution
    private Map<String, String> additionalDeployables = new HashMap<String, String>();

    public ContributionDescription(String url) {
        this(null, url);
    }
    public ContributionDescription(String uri, String url) {
        this.url = url;
        this.uri = uri;
        if (uri == null || uri.length() < 1) {
            this.uri = deriveContributionURI(url);
        }
    }
    
    public String getURI() {
        return uri;
    }
    public String getURL() {
        return url;
    }
    public List<String> getDeployables() {
        return deployables;
    }
    public void setDeployables(List<String> deployables) {
        this.deployables = deployables;
    }
    public List<String> getDependentContributionURIs() {
        return dependentContributionURIs;
    }
    public Map<String, String> getAdditionalDeployables() {
        return additionalDeployables;
    }
    public List<String> getJavaExports() {
        return javaExports;
    }
    public List<String> getNamespaceExports() {
        return namespaceExports;
    }
    public List<String> getJavaImports() {
        return javaImports;
    }
    public List<String> getNamespaceImports() {
        return namespaceImports;
    }
    
    public void configureMetaData(Contribution contribution) {

        if (contribution.getDeployables() != null) {
            for (Composite composite : contribution.getDeployables()) {
                getDeployables().add(composite.getURI());
            }
        }

        if (contribution.getExports() != null) {
            for (Export export : contribution.getExports()) {
                // TODO: Handle these and others in a more extensible way
                if (export instanceof JavaExport) {
                    getJavaExports().add(((JavaExport)export).getPackage());
                } else if (export instanceof NamespaceExport) {
                    getNamespaceExports().add(((NamespaceExport)export).getNamespace());
                } 
            }
        }
        
        if (contribution.getImports() != null) {
            for (Import imprt : contribution.getImports()) {
                // TODO: Handle these and others in a more extensible way
                if (imprt instanceof JavaImport) {
                    getJavaImports().add(((JavaImport)imprt).getPackage());
                } else if (imprt instanceof NamespaceImport) {
                    getNamespaceImports().add(((NamespaceImport)imprt).getNamespace());
                } 
            }
        }
    }
    
    /**
     * Derives a URI for the contribution based on its URL
     */
    protected String deriveContributionURI(String contributionURL) {
        String uri = null;
        try {
            File f = new File(contributionURL);
            if ("classes".equals(f.getName()) && "target".equals(f.getParentFile().getName())) {
                uri = f.getParentFile().getParentFile().getName();
            } else {
                uri = f.getName();
            }
        } catch (Exception e) {
            // ignore
        }
        if (uri == null) {
            uri = contributionURL;
        }
        if (uri.endsWith(".zip") || uri.endsWith(".jar")) {
            uri = uri.substring(0, uri.length() - 4);
        }
        if (uri.endsWith("SNAPSHOT")) {
            uri = uri.substring(0, uri.lastIndexOf('-'));
            uri = uri.substring(0, uri.lastIndexOf('-'));
        }
        return uri;
    }
}
