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
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.implementation.data.DATAImplementation;
import org.apache.tuscany.sca.implementation.data.DATAImplementationFactory;
import org.apache.tuscany.sca.implementation.data.DATAArtifactProcessor;
import org.apache.tuscany.sca.implementation.data.config.ConnectionInfo;
import org.apache.tuscany.sca.implementation.data.config.ConnectionProperties;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.introspect.DefaultJavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.sca.interfacedef.java.introspect.ExtensibleJavaInterfaceIntrospector;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceIntrospector;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;

/**
 * @version $Rev: 538445 $ $Date: 2007-05-15 23:20:37 -0700 (Tue, 15 May 2007) $
 */
public class DATAImplementationProcessorTestCase extends TestCase {
    
    protected static final QName IMPLEMENTATION_DATA = new QName(Constants.SCA10_NS, "implementation.data");

    private static final String COMPOSITE_USING_DATASOURCE =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" targetNamespace=\"http://data\" name=\"data\">"
            + " <component name=\"DataComponent\">"
            + "   <implementation.data table=\"tableName\">"
            + "      <connectionInfo datasource=\"dataSource\"/>"
            + "   </implementation.data>"
            + "</component>";

    private static final String COMPOSITE_USING_CONNECTION_PROPERTIES =            
            "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" targetNamespace=\"http://data\" name=\"data\">"
            + " <component name=\"DataComponent\">"
            + "   <implementation.data table=\"tableName\">"
            + "      <connectionInfo>"
            + "         <connectionProperties"
            + "            driverClass=\"driverClass\""
            + "            databaseURL=\"databaseURL\""
            + "            loginTimeout=\"1\"/>"
            + "      </connectionInfo>"
            + "   </implementation.data>"
            + "</component>";

    private XMLInputFactory xmlFactory;
    private DATAImplementationFactory dataFactory;

    protected void setUp() throws Exception {
        super.setUp();
        xmlFactory = XMLInputFactory.newInstance();
        
        JavaInterfaceFactory javaFactory = new DefaultJavaInterfaceFactory();
        JavaInterfaceIntrospectorExtensionPoint visitors = new DefaultJavaInterfaceIntrospectorExtensionPoint();
        JavaInterfaceIntrospector introspector = new ExtensibleJavaInterfaceIntrospector(javaFactory, visitors);

        dataFactory = new DATAImplementationFactory(new DefaultAssemblyFactory(), new DefaultJavaInterfaceFactory(), introspector);
        
    }

    public void testLoadCompositeUsingDatasource() throws Exception {
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(COMPOSITE_USING_DATASOURCE));
        
        DATAArtifactProcessor dataProcessor = 
            new DATAArtifactProcessor(dataFactory);
        
        while(true) {
            int event = reader.next();
            if(event == XMLStreamConstants.START_ELEMENT && IMPLEMENTATION_DATA.equals(reader.getName())) {
                break;
            }
        }

        DATAImplementation implementation = dataProcessor.read(reader);
        
        assertNotNull(implementation);
        assertEquals("tableName", implementation.getTable());

        ConnectionInfo connInfo = implementation.getConnectionInfo();
        assertNotNull(connInfo);
        assertEquals("dataSource", connInfo.getDataSource());
        
        ConnectionProperties connProperties = connInfo.getConnectionProperties();
        assertNull(connProperties);
    }

    public void testLoadCompositeUsingConnectionProperties() throws Exception {
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(COMPOSITE_USING_CONNECTION_PROPERTIES));

        DATAArtifactProcessor dataProcessor = 
            new DATAArtifactProcessor(dataFactory);
        
        while(true) {
            int event = reader.next();
            if(event == XMLStreamConstants.START_ELEMENT && IMPLEMENTATION_DATA.equals(reader.getName())) {
                break;
            }
        }

        DATAImplementation implementation = dataProcessor.read(reader);
        
        assertNotNull(implementation);
        assertEquals("tableName", implementation.getTable());

        ConnectionInfo connInfo = implementation.getConnectionInfo();
        assertNotNull(connInfo);
        assertNull("dataSource", connInfo.getDataSource());
        
        ConnectionProperties connProperties = connInfo.getConnectionProperties();
        assertNotNull(connProperties);
        assertEquals("driverClass",connProperties.getDriverClass());
        assertEquals("databaseURL",connProperties.getDatabaseURL());
        assertEquals(1,connProperties.getLoginTimeout());
    }    
}
