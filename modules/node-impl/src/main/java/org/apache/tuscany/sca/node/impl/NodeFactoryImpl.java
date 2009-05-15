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

package org.apache.tuscany.sca.node.impl;

import static org.apache.tuscany.sca.node.impl.NodeUtil.openStream;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.apache.tuscany.sca.node.configuration.NodeConfigurationFactory;
import org.apache.tuscany.sca.node.configuration.xml.NodeConfigurationProcessor;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Default implementation of an SCA node factory.
 *
 * @version $Rev$ $Date$
 */
public class NodeFactoryImpl extends NodeFactory {
    public NodeFactoryImpl() {
    }

    @Override
    public Node createNode(String configurationURI) {
        try {
            URL url = new URL(configurationURI);
            InputStream is = openStream(url);
            NodeConfiguration configuration = loadConfiguration(is);
            is.close();
            return new NodeImpl(configuration);
        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    @Override
    public Node createNode(String compositeURI, Contribution... contributions) {
        NodeConfigurationFactory factory = this;
        NodeConfiguration configuration = factory.createNodeConfiguration();
        for (Contribution c : contributions) {
            configuration.addContribution(c.getURI(), c.getLocation());
        }
        if (compositeURI != null && configuration.getContributions().size() > 0) {
            configuration.getContributions().get(0).addDeploymentComposite(NodeUtil.createURI(compositeURI));
        }
        return new NodeImpl(configuration);
    }

    @Override
    public Node createNode(String compositeURI, String compositeContent, Contribution... contributions) {
        NodeConfigurationFactory factory = this;
        NodeConfiguration configuration = factory.createNodeConfiguration();
        for (Contribution c : contributions) {
            configuration.addContribution(c.getURI(), c.getLocation());
        }
        if (compositeContent != null && configuration.getContributions().size() > 0) {
            configuration.getContributions().get(0).addDeploymentComposite(compositeContent);
        }
        return new NodeImpl(configuration);
    }

    @Override
    public Node createNode() {
        String location =
            ContributionLocationHelper
                .getContributionLocation(org.apache.tuscany.sca.contribution.Contribution.SCA_CONTRIBUTION_META);
        if (location == null) {
            location =
                ContributionLocationHelper
                    .getContributionLocation(org.apache.tuscany.sca.contribution.Contribution.SCA_CONTRIBUTION_GENERATED_META);
        }
        if (location == null) {
            throw new ServiceRuntimeException("No SCA contributions are found on the classpath");
        }
        return createNode(null, new Contribution("http://contributions/default", location));
    }

    @Override
    public Node createNode(NodeConfiguration configuration) {
        return new NodeImpl(configuration);
    }

    /**
     * @param <T>
     * @param factory
     * @return
     * @throws Exception
     */
    private <T> T getFactory(Class<T> factory) throws Exception {
        ServiceDeclaration sd = ServiceDiscovery.getInstance().getFirstServiceDeclaration(factory.getName());
        if (sd != null) {
            return factory.cast(sd.loadClass().newInstance());
        } else {
            return factory.cast(factory.getMethod("newInstance").invoke(null));
        }
    }

    @Override
    public NodeConfiguration loadConfiguration(InputStream xml) {
        try {
            XMLInputFactory inputFactory = getFactory(XMLInputFactory.class);
            XMLOutputFactory outputFactory = getFactory(XMLOutputFactory.class);
            XMLStreamReader reader = inputFactory.createXMLStreamReader(xml);
            NodeConfigurationProcessor processor = new NodeConfigurationProcessor(this, inputFactory, outputFactory);
            reader.nextTag();
            NodeConfiguration config = processor.read(reader);
            xml.close();
            return config;
        } catch (Throwable e) {
            throw new ServiceRuntimeException(e);
        }
    }
}
