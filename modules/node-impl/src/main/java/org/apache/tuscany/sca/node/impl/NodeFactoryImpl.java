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

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
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
        return new NodeImpl(configurationURI);
    }

    @Override
    public Node createNode(String compositeURI, Contribution... contributions) {
        return new NodeImpl(compositeURI, contributions);
    }

    @Override
    public Node createNode(String compositeURI, String compositeContent, Contribution... contributions) {
        return new NodeImpl(compositeURI, compositeContent, contributions);
    }

    @Override
    public Node createNode() {
        return new NodeImpl();
    }

    @Override
    public Node createNode(NodeConfiguration configuration) {
        return null;
    }

    @Override
    public NodeConfiguration loadConfiguration(InputStream xml) {
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
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
