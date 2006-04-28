/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.loader.assembly;

import static org.apache.tuscany.core.loader.assembly.AssemblyConstants.INTERFACE_WSDL;

import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.core.loader.impl.WSDLDefinitionRegistryImpl;
import org.apache.tuscany.model.types.wsdl.WSDLServiceContract;
import org.apache.tuscany.sdo.util.SDOUtil;

import commonj.sdo.helper.XSDHelper;

/**
 * @version $Rev$ $Date$
 */
public class InterfaceWSDLLoaderInterfaceStylesTestCase extends LoaderTestSupport {
    private WSDLDefinitionRegistryImpl wsdlRegistry;
    private ClassLoader oldCL;

    public void testInterface() throws Exception {
        wsdlRegistry.loadDefinition("http://www.interfacestyles.org", getClass().getResource("interfacestyles.wsdl"));
        String xml = "<interface.wsdl xmlns='http://www.osoa.org/xmlns/sca/0.9' interface='http://www.interfacestyles.org#TestInterfaceStylesService'></interface.wsdl>";
        XMLStreamReader reader = getReader(xml);
        WSDLServiceContract sc = (WSDLServiceContract) registry.load(reader, loaderContext);
        reader.require(XMLStreamConstants.END_ELEMENT, INTERFACE_WSDL.getNamespaceURI(), INTERFACE_WSDL.getLocalPart());
        assertEquals(XMLStreamConstants.END_DOCUMENT, reader.next());
        assertNotNull(sc);
        
        sc.initialize(modelContext);
        
        Class scInterface = sc.getInterface();
        assertNotNull(scInterface);
        
        assertNotNull(scInterface.getMethod("getAccountReportWrapped0", new Class[0]));
        assertNotNull(scInterface.getMethod("getAccountReportWrapped1", new Class[] {String.class}));
        assertNotNull(scInterface.getMethod("getAccountReportWrappedN", new Class[] {String.class, int.class}));
        assertNotNull(scInterface.getMethod("getAccountReportBare0", new Class[0]));
        assertNotNull(scInterface.getMethod("getAccountReportBare1Simple", new Class[]{String.class}));
        assertNotNull(scInterface.getMethod("getAccountReportBare1Complex", new Class[]{Object.class}));
        
    }

    protected void setUp() throws Exception {
        oldCL = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        super.setUp();
        
        wsdlRegistry = new WSDLDefinitionRegistryImpl();
        wsdlRegistry.setMonitor(NULL_MONITOR);
        URL wsdlURL = getClass().getResource("interfacestyles.wsdl");
        wsdlRegistry.loadDefinition("http://www.interfacestyles.org", wsdlURL);
        InterfaceWSDLLoader loader = new InterfaceWSDLLoader();
        loader.setWsdlRegistry(wsdlRegistry);
        registerLoader(loader);
        
        InputStream xsdInputStream = wsdlURL.openStream();
        try {
            XSDHelper xsdHelper = SDOUtil.createXSDHelper(modelContext.getTypeHelper());
            xsdHelper.define(xsdInputStream, null);
        } finally {
            xsdInputStream.close();
        }
    }
    
    protected void tearDown() throws Exception {
        Thread.currentThread().setContextClassLoader(oldCL);
        super.tearDown();
    }

    private static final WSDLDefinitionRegistryImpl.Monitor NULL_MONITOR = new WSDLDefinitionRegistryImpl.Monitor() {
        public void readingWSDL(String namespace, URL location) {
        }

        public void cachingDefinition(String namespace, URL location) {
        }
    };
}
