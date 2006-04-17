/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.model.types.wsdl.impl;

import java.util.List;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.objectweb.asm.ClassWriter;
import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.V1_5;
import org.objectweb.asm.Type;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.impl.ServiceContractImpl;
import org.apache.tuscany.model.types.wsdl.WSDLServiceContract;
import org.apache.tuscany.model.util.XMLNameUtil;

/**
 * An implementation of WSDLServiceContract.
 */
public class WSDLServiceContractImpl extends ServiceContractImpl implements WSDLServiceContract {

    private PortType portType;

    private PortType callbackPortType;

    private static final String[] EMPTY_STRINGS = new String[0];

    /**
     * Constructor
     */
    public WSDLServiceContractImpl() {
    }

    public PortType getPortType() {
        return portType;
    }

    public void setPortType(PortType portType) {
        checkNotFrozen();
        this.portType = portType;
    }

    public PortType getCallbackPortType() {
        return callbackPortType;
    }

    public void setCallbackPortType(PortType portType) {
        checkNotFrozen();
        callbackPortType = portType;
    }

    /**
     * @see org.apache.tuscany.model.assembly.impl.ExtensibleImpl#initialize(org.apache.tuscany.model.assembly.AssemblyContext)
     */
    public void initialize(AssemblyContext modelContext) {
        if (isInitialized())
            return;
        super.initialize(modelContext);

        // Load the Java interface for the portType
        if (portType != null && getInterface() == null) {
            QName qname = portType.getQName();
            String interfaceName = XMLNameUtil.getFullyQualifiedClassNameFromQName(qname.getNamespaceURI(), qname.getLocalPart());
            Class<?> interfaceClass;
            try {
                // Load the interface
                interfaceClass = modelContext.getApplicationResourceLoader().loadClass(interfaceName);
            } catch (ClassNotFoundException e) {
                // Generate the interface on the fly
                interfaceClass = generateJavaInterface(modelContext.getApplicationResourceLoader(), portType, interfaceName);
            }
            super.setInterface(interfaceClass);
        }

        // Load the Java interface for the callback portType
        if (callbackPortType != null && getCallbackInterface() == null) {
            QName qname = callbackPortType.getQName();
            String interfaceName = XMLNameUtil.getFullyQualifiedClassNameFromQName(qname.getNamespaceURI(), qname.getLocalPart());
            Class<?> interfaceClass;
            try {
                // Load the interface
                interfaceClass = modelContext.getApplicationResourceLoader().loadClass(interfaceName);
            } catch (ClassNotFoundException e) {
                // Generate the interface on the fly
                interfaceClass = generateJavaInterface(modelContext.getApplicationResourceLoader(), portType, interfaceName);
            }
            super.setCallbackInterface(interfaceClass);
        }
    }

    /**
     * Generate a Java interface from a WSDL portType.
     *
     * @param portType
     * @param interfaceName
     * @return a Java interface that provides the same service contract as the WSDL portType
     */
    @SuppressWarnings("unchecked")
    private static Class<?> generateJavaInterface(ResourceLoader resourceLoader, PortType portType, String interfaceName) {
        ClassWriter cw = new ClassWriter(false);

        // Generate the interface
        interfaceName = interfaceName.replace('.', '/');
        cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, interfaceName, null, "java/lang/Object", EMPTY_STRINGS);

        // Generate methods from the WSDL operations
        for (Operation operation : (List<Operation>) portType.getOperations()) {
            String methodName = XMLNameUtil.getJavaNameFromXMLName(operation.getName(), false);

            // FIXME integrate XSD to Java type mapping here
            String inputType = Type.getDescriptor(String.class);
            String outputType = Type.getDescriptor(String.class);

            cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, methodName, '(' + inputType + ')' + outputType, null, null).visitEnd();
        }

        // Generate the bytecodes
        cw.visitEnd();
        byte[] bytes = cw.toByteArray();

        // Add the class to the resource loader

        return resourceLoader.addClass(bytes);
    }

}
