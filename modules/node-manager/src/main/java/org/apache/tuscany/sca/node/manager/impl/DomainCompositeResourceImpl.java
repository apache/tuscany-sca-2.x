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

package org.apache.tuscany.sca.node.manager.impl;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.WebApplicationException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.extensibility.NodeActivator;
import org.apache.tuscany.sca.node.extensibility.NodeExtension;
import org.apache.tuscany.sca.node.manager.DomainCompositeResource;

public class DomainCompositeResourceImpl implements NodeActivator, DomainCompositeResource {
    private static Map<String, NodeExtension> nodeMap = new ConcurrentHashMap<String,NodeExtension>();

    public void nodeStarted(Node node) {
        NodeExtension nodeExtension = (NodeExtension) node;
        nodeMap.put(nodeExtension.getDomainURI(), nodeExtension);
    }

    public void nodeStopped(Node node) {
        NodeExtension nodeExtension = (NodeExtension) node;
        nodeMap.remove(nodeExtension.getDomainURI());
    }

    public String getDomainComposite(String domainURI) {
        if( ! nodeMap.containsKey(domainURI)) {
            throw new WebApplicationException(404);
        }

        NodeExtension node = nodeMap.get(domainURI);
        Composite composite = node.getDomainComposite();

        //set name, as it's empty by default
        composite.setName(new QName("", "Domain"));

        ExtensionPointRegistry registry = node.getExtensionPointRegistry();
        StAXArtifactProcessorExtensionPoint xmlProcessors =
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        StAXArtifactProcessor<Composite>  compositeProcessor =
            xmlProcessors.getProcessor(Composite.class);

        return writeComposite(composite, registry, compositeProcessor);
    }


    private String writeComposite(Composite composite, ExtensionPointRegistry registry, StAXArtifactProcessor<Composite> compositeProcessor){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLOutputFactory outputFactory =
            registry.getExtensionPoint(FactoryExtensionPoint.class)
                .getFactory(XMLOutputFactory.class);

        try {
            compositeProcessor.write(composite, outputFactory.createXMLStreamWriter(bos), new ProcessorContext(registry));
        } catch(Exception ex) {
            return ex.toString();
        }

        String result = bos.toString();

        // write out and nested composites
        for (Component component : composite.getComponents()) {
            if (component.getImplementation() instanceof Composite) {
                result += "\n<!-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX -->\n" +
                           writeComposite((Composite)component.getImplementation(), registry,
                                          compositeProcessor);
            }
        }
        return result;
    }
}
