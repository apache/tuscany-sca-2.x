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

package org.apache.tuscany.sca.node.configuration.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.node.configuration.BindingConfiguration;
import org.apache.tuscany.sca.node.configuration.ContributionConfiguration;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;

/**
 *
 */
class NodeConfigurationImpl implements NodeConfiguration {
    private String uri;
    private String domainURI;
    private List<ContributionConfiguration> contributions = new ArrayList<ContributionConfiguration>();
    private List<BindingConfiguration> bindings = new ArrayList<BindingConfiguration>();

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public String getDomainURI() {
        return domainURI;
    }

    public void setDomainURI(String domainURI) {
        this.domainURI = domainURI;
    }

    public List<ContributionConfiguration> getContributions() {
        return contributions;
    }

    public List<BindingConfiguration> getBindings() {
        return bindings;
    }


}
