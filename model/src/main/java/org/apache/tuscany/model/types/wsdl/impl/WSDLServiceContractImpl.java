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

import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.V1_5;

import java.util.ArrayList;
import java.util.List;

import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.impl.ServiceContractImpl;
import org.apache.tuscany.model.types.wsdl.WSDLServiceContract;
import org.apache.tuscany.model.util.XMLNameUtil;
import org.apache.tuscany.sdo.util.SDOUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import commonj.sdo.Property;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XSDHelper;

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
                interfaceClass = generateJavaInterface(modelContext.getTypeHelper(), modelContext.getApplicationResourceLoader(), portType, interfaceName);
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
                interfaceClass = generateJavaInterface(modelContext.getTypeHelper(), modelContext.getApplicationResourceLoader(), portType, interfaceName);
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
    private static Class<?> generateJavaInterface(TypeHelper typeHelper, ResourceLoader resourceLoader, PortType portType, String interfaceName) {

        ClassLoader cl=Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(resourceLoader.getClassLoader());
            
            // Create an XSD helper
            XSDHelper xsdHelper = SDOUtil.createXSDHelper(typeHelper);
            
            ClassWriter cw = new ClassWriter(false);

            // Generate the interface
            interfaceName = interfaceName.replace('.', '/');
            cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, interfaceName, null, "java/lang/Object", EMPTY_STRINGS);

            // Generate methods from the WSDL operations
            for (Operation operation : (List<Operation>) portType.getOperations()) {
                
                //FIXME Workaround for TUSCANY-170, we will need to make this consistent with the algorithm used by Axis2 WSDL2Java
                // to generate method names from operations names
                //String methodName = XMLNameUtil.getJavaNameFromXMLName(operation.getName(), false);
                String methodName = operation.getName();
                
                // FIXME later we may want to wwitch to use the Axis2 WSDL2Java (not to generate the Java source,
                // just to figure the WSDL to Java mapping)
                
                // Derive the method signature from the input message part (and check if it's a doc-wrapped or doc-bare operation)
                List<Class> inputTypes=new ArrayList<Class>();
                boolean wrapped = false;
                if (operation.getInput() != null && operation.getInput().getMessage()!=null && !operation.getInput().getMessage().getParts().isEmpty()) {
                    QName qname=((Part)operation.getInput().getMessage().getParts().values().iterator().next()).getElementName();
                    if (qname!=null) {
                        Property property = xsdHelper.getGlobalProperty(qname.getNamespaceURI(), qname.getLocalPart(), true);
                        commonj.sdo.Type type = property.getType();
                        if (property.getName().equals(operation.getName())) {
                            String localName = xsdHelper.getLocalName(type);
                            if (localName.indexOf("_._")!=-1) {
                                for (Property param : (List<Property>)type.getProperties()) {
                                    Class inputType = param.getType().getInstanceClass();
                                    if (inputType == null)
                                        inputType = Object.class;
                                    inputTypes.add(inputType);
                                }
                                wrapped=true;
                            }
                        }

                        // Bare doc style
                        if (!wrapped) {
                            Class inputType = type.getInstanceClass();
                            if (inputType == null)
                                inputType = Object.class;
                            inputTypes.add(inputType);
                        }
                        
                    } else {
                        // FIXME only support elements for now 
                    }
                }
                
                // Derive the return type from the output message part (also support doc-wrapped and doc-bare here)
                Class outputType=Void.class;
                if (operation.getOutput() != null && operation.getOutput().getMessage()!=null && !operation.getOutput().getMessage().getParts().isEmpty()) {
                    QName qname=((Part)operation.getOutput().getMessage().getParts().values().iterator().next()).getElementName();
                    if (qname!=null) {
                        Property property = xsdHelper.getGlobalProperty(qname.getNamespaceURI(), qname.getLocalPart(), true);
                        commonj.sdo.Type type = property.getType();
                        if (wrapped) {
                            if (!type.getProperties().isEmpty()) {
                                outputType=((Property)type.getProperties().get(0)).getType().getInstanceClass();
                                if (outputType==null)
                                    outputType=Object.class;
                            }
                        } else {
                            outputType = type.getInstanceClass();
                            if (outputType==null)
                                outputType=Object.class;
                        }
                    } else {
                        // FIXME only support elements for now 
                    }
                }

                // FIXME integrate XSD to Java type mapping here
                StringBuffer inputSignature=new StringBuffer();
                for (Class inputType : inputTypes) {
                    inputSignature.append(Type.getDescriptor(inputType));
                }
                String outputSignature = Type.getDescriptor(outputType);

                cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, methodName, '(' + inputSignature.toString() + ')' + outputSignature, null, null).visitEnd();
            }

            // Generate the bytecodes
            cw.visitEnd();
            byte[] bytes = cw.toByteArray();

            // Add the class to the resource loader

            return resourceLoader.addClass(bytes);
            
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
        
    }

}
