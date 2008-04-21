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
package org.apache.tuscany.sca.tools.inspector;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.node.SCANode2;
import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.w3c.dom.Node;


/**
 * Prints out the composite XML.
 */
public class AssemblyInspector {
    

    public String assemblyAsString(SCANode2 node) {
        StringBuffer assemblyString = new StringBuffer();
        
        // get at the node internals
        Composite composite = ((NodeImpl)node).getComposite();
        ExtensionPointRegistry registry = ((NodeImpl)node).getExtensionPointRegistry();
        
        // Get the output factory 
        ModelFactoryExtensionPoint modelFactories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        XMLOutputFactory outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        StAXArtifactProcessorExtensionPoint staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        StAXArtifactProcessor<Composite> compositeProcessor = (StAXArtifactProcessor<Composite>)staxProcessors.getProcessor(Composite.class);
        
        // Write the composite
        try {
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(System.out);
            compositeProcessor.write(composite, writer);
        } catch (Exception e) {
            assemblyString.append(e.toString());
        }

        return assemblyString.toString();
    }
    

}
