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

import org.apache.tuscany.sca.node.configuration.BindingConfiguration;
import org.apache.tuscany.sca.node.configuration.ContributionConfiguration;
import org.apache.tuscany.sca.node.configuration.DeploymentComposite;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.apache.tuscany.sca.node.configuration.NodeConfigurationFactory;

/**
 * The factory to create java models related to the node configuration
 */
public class NodeConfigurationFactoryImpl implements NodeConfigurationFactory {
    /**
     * Create a new instance of NodeConfiguration
     * @return
     */
    public NodeConfiguration createNodeConfiguration() {
        return new NodeConfigurationImpl();
    }

    /**
     * Create a new instance of ContributionConfiguration
     * @return
     */
    public ContributionConfiguration createContributionConfiguration() {
        return new ContributionConfigurationImpl();
    }

    /**
     * Create a new instance of BindingConfiguration
     * @return
     */
    public BindingConfiguration createBindingConfiguration() {
        return new BindingConfigurationImpl();
    }

    /**
     * Create a new instance of DeploymentComposite
     * @return
     */
    public DeploymentComposite createDeploymentComposite() {
        return new DeploymentCompositeImpl();
    }
}
