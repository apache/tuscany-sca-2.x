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
package org.apache.tuscany.sca.binding.axis2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.xml.WSDLLocator;
import javax.wsdl.xml.WSDLReader;

import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.wsdl.DefaultWSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.interfacedef.wsdl.impl.InvalidWSDLException;
import org.apache.tuscany.sca.interfacedef.wsdl.impl.WSDLOperationIntrospectorImpl;
import org.apache.tuscany.sca.interfacedef.wsdl.xml.XMLDocumentHelper;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.java2wsdl.Java2WSDLBuilder;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * Utility methods to create WSDL objects from Java interfaces
 */
public class Java2WSDLHelper {

    /**
     * Create a WSDLInterfaceContract from a JavaInterfaceContract
     */
    public static WSDLInterfaceContract createWSDLInterfaceContract(JavaInterfaceContract contract) {
        JavaInterface iface = (JavaInterface)contract.getInterface();
        Definition def = Java2WSDLHelper.createDefinition(iface.getJavaClass());

        DefaultWSDLFactory wsdlFactory = new DefaultWSDLFactory();

        WSDLInterfaceContract wsdlContract = wsdlFactory.createWSDLInterfaceContract();
        WSDLInterface wsdlInterface = wsdlFactory.createWSDLInterface();

        wsdlContract.setInterface(wsdlInterface);
        WSDLDefinition wsdlDefinition = new DefaultWSDLFactory().createWSDLDefinition();
        wsdlDefinition.setDefinition(def);
        wsdlInterface.setWsdlDefinition(wsdlDefinition);
        wsdlInterface.setRemotable(true);
        wsdlInterface.setConversational(contract.getInterface().isConversational());
        wsdlInterface.setUnresolved(false);
        wsdlInterface.setRemotable(true);
        PortType portType = (PortType)def.getAllPortTypes().values().iterator().next();
        wsdlInterface.setPortType(portType);

        readInlineSchemas(def, wsdlDefinition.getInlinedSchemas());

        try {
            for (Operation op : iface.getOperations()) {
                Operation clonedOp = (Operation)op.clone();
                clonedOp.setDataBinding(null);
                for (DataType<?> dt : clonedOp.getInputType().getLogical()) {
                    dt.setDataBinding(null);
                }
                clonedOp.getOutputType().setDataBinding(null);
                for (DataType<?> dt : clonedOp.getFaultTypes()) {
                    dt.setDataBinding(null);
                }
                clonedOp.setWrapperStyle(true);
                javax.wsdl.Operation wsdlOp = portType.getOperation(op.getName(), null, null);
                WSDLOperationIntrospectorImpl opx =
                    new WSDLOperationIntrospectorImpl(wsdlFactory, wsdlOp, wsdlDefinition.getInlinedSchemas(), null,
                                                      null);
                clonedOp.setWrapper(opx.getWrapper().getWrapperInfo());

                wsdlInterface.getOperations().add(clonedOp);
            }
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        } catch (InvalidWSDLException e) {
            throw new RuntimeException(e);
        }

        return wsdlContract;
    }

    protected static void readInlineSchemas(Definition definition, XmlSchemaCollection schemaCollection) {
        Types types = definition.getTypes();
        if (types != null) {
            for (Object ext : types.getExtensibilityElements()) {
                if (ext instanceof Schema) {
                    Element element = ((Schema)ext).getElement();
                    schemaCollection.setBaseUri(((Schema)ext).getDocumentBaseURI());
                    schemaCollection.read(element, element.getBaseURI());
                }
            }
        }
        for (Object imports : definition.getImports().values()) {
            List<?> impList = (List<?>)imports;
            for (Object i : impList) {
                javax.wsdl.Import anImport = (javax.wsdl.Import)i;
                // Read inline schemas
                if (anImport.getDefinition() != null) {
                    readInlineSchemas(anImport.getDefinition(), schemaCollection);
                }
            }
        }
    }

    /**
     * Create a WSDL4J Definition object from a Java interface
     */
    protected static Definition createDefinition(Class<?> javaInterface) {

        String className = javaInterface.getName();
        ClassLoader cl = javaInterface.getClassLoader();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Java2WSDLBuilder builder = new Java2WSDLBuilder(os, className, cl);

        try {
            builder.generateWSDL();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {

            WSDLReader reader = javax.wsdl.factory.WSDLFactory.newInstance().newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", false);
            reader.setFeature("javax.wsdl.importDocuments", true);

            WSDLLocatorImpl locator = new WSDLLocatorImpl(new ByteArrayInputStream(os.toByteArray()));
            Definition definition = reader.readWSDL(locator);
            
            // remove the soap 1.2 port as we don't use that (yet) 
            Service service = (Service)definition.getServices().values().iterator().next();
            Map<?,?> ports = service.getPorts();
            for (Object o : ports.keySet()) {
                if (((String)o).endsWith("SOAP12port")) {
                    Port p = (Port) ports.remove(o);
                    definition.removeBinding(p.getBinding().getQName());
                    break;
                }
            }

            return definition;

        } catch (WSDLException e) {
            throw new RuntimeException(e);
        }
    }
}

class WSDLLocatorImpl implements WSDLLocator {
    private InputStream inputStream;
    private String base = "http://";
    private String latestImportURI;

    public WSDLLocatorImpl(InputStream is) {
        this.inputStream = is;
    }

    public void close() {
        try {
            inputStream.close();
        } catch (IOException e) {
            // Ignore
        }
    }

    public InputSource getBaseInputSource() {
        try {
            return XMLDocumentHelper.getInputSource(new URL(base), inputStream);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String getBaseURI() {
        return base;
    }

    public InputSource getImportInputSource(String parentLocation, String importLocation) {
        return null;
    }

    public String getLatestImportURI() {
        return latestImportURI;
    }

}
