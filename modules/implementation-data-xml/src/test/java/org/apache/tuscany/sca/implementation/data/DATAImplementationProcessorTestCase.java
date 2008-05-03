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

package org.apache.tuscany.sca.implementation.data;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeBuilderImpl;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.data.engine.config.ConnectionInfo;
import org.apache.tuscany.sca.data.engine.config.ConnectionProperties;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;

/**
 * @version $Rev: 538445 $ $Date: 2007-05-15 23:20:37 -0700 (Tue, 15 May 2007) $
 */
public class DATAImplementationProcessorTestCase extends TestCase {
    
    protected static final QName IMPLEMENTATION_DATA = new QName(Constants.SCA10_TUSCANY_NS, "implementation.data.xml");

    private static final String COMPOSITE_USING_DATASOURCE =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.0\" targetNamespace=\"http://data\" name=\"data\">"
            + " <component name=\"DataComponent\">"
            + "   <tuscany:implementation.data.xml>"
            + "      <tuscany:connectionInfo datasource=\"dataSource\"/>"
            + "   </tuscany:implementation.data.xml>"
            + " </component>"
            + "</composite>";

    private static final String COMPOSITE_USING_CONNECTION_PROPERTIES =            
            "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.0\" targetNamespace=\"http://data\" name=\"data\">"
            + " <component name=\"DataComponent\">"
            + "   <tuscany:implementation.data.xml>"
            + "      <tuscany:connectionInfo>"
            + "         <tuscany:connectionProperties"
            + "            driverClass=\"driverClass\""
            + "            databaseURL=\"databaseURL\""
            + "            loginTimeout=\"1\"/>"
            + "      </tuscany:connectionInfo>"
            + "   </tuscany:implementation.data.xml>"
            + " </component>"
            + "</composite>";

    private XMLInputFactory inputFactory;
    private StAXArtifactProcessor<Object> staxProcessor;
    private CompositeBuilder compositeBuilder;

    @Override
    protected void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        inputFactory = XMLInputFactory.newInstance();
        StAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint(extensionPoints);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, null);
        
        ModelFactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        SCABindingFactory scaBindingFactory = modelFactories.getFactory(SCABindingFactory.class);
        InterfaceContractMapper mapper = new InterfaceContractMapperImpl();
        IntentAttachPointTypeFactory attachPointTypeFactory = modelFactories.getFactory(IntentAttachPointTypeFactory.class);
        compositeBuilder = new CompositeBuilderImpl(assemblyFactory, scaBindingFactory, attachPointTypeFactory, mapper, null);
    }

    public void testLoadCompositeUsingDatasource() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(COMPOSITE_USING_DATASOURCE));
        
        Composite composite = (Composite)staxProcessor.read(reader);
        DATAImplementation implementation = (DATAImplementation)composite.getComponents().get(0).getImplementation();
        assertNotNull(implementation);

        ConnectionInfo connInfo = implementation.getConnectionInfo();
        assertNotNull(connInfo);
        assertEquals("dataSource", connInfo.getDataSource());
        
        ConnectionProperties connProperties = connInfo.getConnectionProperties();
        assertNull(connProperties);
    }

    public void testLoadCompositeUsingConnectionProperties() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(COMPOSITE_USING_CONNECTION_PROPERTIES));

        Composite composite = (Composite)staxProcessor.read(reader);
        DATAImplementation implementation = (DATAImplementation)composite.getComponents().get(0).getImplementation();
        assertNotNull(implementation);

        ConnectionInfo connInfo = implementation.getConnectionInfo();
        assertNotNull(connInfo);
        assertNull("dataSource", connInfo.getDataSource());
        
        ConnectionProperties connProperties = connInfo.getConnectionProperties();
        assertNotNull(connProperties);
        assertEquals("driverClass",connProperties.getDriverClass());
        assertEquals("databaseURL",connProperties.getDatabaseURL());
        assertEquals(1,connProperties.getLoginTimeout().intValue());
    }    
}
