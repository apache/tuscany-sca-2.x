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
package org.apache.tuscany.binding.axis2.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPHeader;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.xml.namespace.QName;

/**
 * Metadata for a WSDL operation
 */
@SuppressWarnings({"all"})
public class WebServiceOperationMetaData implements Serializable {
    private static final long serialVersionUID = 2425306250256227724L;

    // WSDL BindingDefinition and BindingOperation
    private Binding binding;
    private BindingOperation bindingOperation;
    // Fields to cache derived metadata
    private transient Set<Part> inputHeaderParts;
    private transient Set<Part> outputHeaderParts;
    private transient String style;
    private transient String use;
    private transient String soapAction;
    private transient List<Object> signature;
    private String encoding;
    private transient QName rpcOperationName;

    public WebServiceOperationMetaData(Binding binding, BindingOperation bindingOperation) {
        this.binding = binding;
        this.bindingOperation = bindingOperation;
    }

    public WebServiceOperationMetaData(Binding binding, BindingOperation bindingOperation, String style, String use,
                                       String encoding,
                                       String soapAction) {
        this.binding = binding;
        this.bindingOperation = bindingOperation;
        this.style = style;
        this.use = use;
        this.encoding = encoding;
        this.soapAction = soapAction;
    }

    public Set<Part> getInputHeaderParts() {
        if (inputHeaderParts == null) {
            // Build a set of header parts that we need to exclude
            inputHeaderParts = new HashSet<Part>();
            BindingInput bindingInput = bindingOperation.getBindingInput();

            if (bindingInput != null) {
                Operation operation = bindingOperation.getOperation();
                javax.wsdl.Message message = operation.getInput().getMessage();
                List elements = bindingInput.getExtensibilityElements();
                for (Iterator i = elements.iterator(); i.hasNext();) {
                    Object extensibilityElement = i.next();
                    Part part = getPartFromSOAPHeader(message, extensibilityElement);
                    if (part != null) {
                        inputHeaderParts.add(part);
                    }
                }
            }
        }
        return inputHeaderParts;
    }

    public Set<Part> getOutputHeaderParts() {
        if (outputHeaderParts == null) {
            // Build a set of header parts that we need to exclude
            outputHeaderParts = new HashSet<Part>();
            BindingOutput bindingOutput = bindingOperation.getBindingOutput();

            if (bindingOutput != null) {
                Operation operation = bindingOperation.getOperation();
                javax.wsdl.Message message = operation.getOutput().getMessage();
                List elements = bindingOutput.getExtensibilityElements();
                for (Iterator i = elements.iterator(); i.hasNext();) {
                    Object extensibilityElement = i.next();
                    Part part = getPartFromSOAPHeader(message, extensibilityElement);
                    if (part != null) {
                        outputHeaderParts.add(part);
                    }
                }
            }
        }
        return outputHeaderParts;
    }

    private Part getPartFromSOAPHeader(Message message, Object extensibilityElement) {
        Part part = null;
        if (extensibilityElement instanceof SOAPHeader) {
            SOAPHeader soapHeader = (SOAPHeader) extensibilityElement;
            QName msgName = soapHeader.getMessage();
            if (message.getQName().equals(msgName)) {
                part = message.getPart(soapHeader.getPart());
            }
        } else if (extensibilityElement instanceof SOAPHeader) {
            SOAPHeader soapHeader = (SOAPHeader) extensibilityElement;
            QName msgName = soapHeader.getMessage();
            if (message.getQName().equals(msgName)) {
                part = message.getPart(soapHeader.getPart());
            }
        }
        return part;
    }

    public String getStyle() {
        if (style == null) {
            SOAPOperation soapOperation = (SOAPOperation) WebServicePortMetaData
                .getExtensibilityElement(bindingOperation.getExtensibilityElements(),
                    SOAPOperation.class);
            if (soapOperation != null) {
                style = soapOperation.getStyle();
            }
            if (style == null) {
                SOAPBinding soapBinding = WebServicePortMetaData
                    .getExtensibilityElement(binding.getExtensibilityElements(), SOAPBinding.class);
                if (soapBinding != null) {
                    style = soapBinding.getStyle();
                }
            }
            if (style == null) {
                style = "document";
            }
        }
        return style;
    }

    /**
     * Returns the SOAP action for the given operation.
     */
    public String getSOAPAction() {
        if (soapAction == null) {
            final List wsdlBindingOperationExtensions = bindingOperation.getExtensibilityElements();
            final SOAPOperation soapOp =
                WebServicePortMetaData.getExtensibilityElement(wsdlBindingOperationExtensions, SOAPOperation.class);
            if (soapOp != null) {
                soapAction = soapOp.getSoapActionURI();
            }
        }
        return soapAction;
    }

    public QName getRPCOperationName() {
        if (rpcOperationName == null) {
            javax.wsdl.extensions.soap.SOAPBody soapBody = getSOAPBody(true);
            String ns =
                (soapBody != null) ? soapBody.getNamespaceURI() : binding.getPortType().getQName().getNamespaceURI();
            String name = bindingOperation.getOperation().getName();
            rpcOperationName = new QName(ns, name);
        }
        return rpcOperationName;
    }

    private List<String> getSOAPBodyParts(boolean input) {
        javax.wsdl.extensions.soap.SOAPBody soapBody = getSOAPBody(input);
        if (soapBody != null) {
            List parts = soapBody.getParts();
            if (parts != null) {
                List<String> names = new ArrayList<String>();
                for (Iterator i = parts.iterator(); i.hasNext();) {
                    Object part = i.next();
                    if (part instanceof String) {
                        names.add((String) part);
                    } else if (part instanceof Part) {
                        names.add(((Part) part).getName());
                    }
                }
                return names;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private javax.wsdl.extensions.soap.SOAPBody getSOAPBody(boolean input) {
        List elements = null;
        if (input) {
            BindingInput bindingInput = bindingOperation.getBindingInput();
            if (bindingInput == null) {
                return null;
            }
            elements = bindingInput.getExtensibilityElements();
        } else {
            BindingOutput bindingOutput = bindingOperation.getBindingOutput();
            if (bindingOutput == null) {
                return null;
            }
            elements = bindingOutput.getExtensibilityElements();
        }
        javax.wsdl.extensions.soap.SOAPBody soapBody = WebServicePortMetaData.getExtensibilityElement(elements,
            javax.wsdl.extensions.soap.SOAPBody.class);
        return soapBody;
    }

    /**
     * Returns the use attribute
     */
    public String getUse() {
        if (use == null) {
            javax.wsdl.extensions.soap.SOAPBody soapBody = getSOAPBody(true);
            if (soapBody != null) {
                use = soapBody.getUse();
            }
            if (use == null) {
                use = "literal";
            }
        }
        return use;
    }

    @SuppressWarnings("unchecked")
    public String getEncoding() {
        if (encoding == null) {
            javax.wsdl.extensions.soap.SOAPBody soapBody = getSOAPBody(true);
            if (soapBody != null) {
                List<String> styles = (List<String>) soapBody.getEncodingStyles();
                if (styles != null && !styles.isEmpty()) {
                    encoding = styles.get(0);
                }
            }
            if (encoding == null) {
                encoding = "";
            }
        }
        return encoding;
    }

    public boolean isDocLitWrapped() {
        boolean flag = getStyle().equals("document") && getUse().equals("literal");
        if (!flag) {
            return false;
        }
        Message msg = getMessage(true);
        if (msg == null) {
            return false;
        }
        List parts = msg.getOrderedParts(null);
        if (parts.size() != 1) {
            return false;
        }
        Part part = (Part) parts.get(0);
        QName element = part.getElementName();
        if (element == null) {
            return false;
        }
        return element.getLocalPart().equals(bindingOperation.getOperation().getName());
    }

    /*
     * public SOAPMediator createMediator(boolean serverMode) throws SOAPException {
     * // create a new mediator for each invoke for thread-safety
     * boolean rpcStyle = getStyle().equals("rpc"); boolean rpcEncoded = isEncoded();
     * 
     * SOAPMediator mediator = null;
     * 
     * if (!rpcStyle) { // Document mediator = new SOAPDocumentLiteralMediatorImpl(this, serverMode);
     * } else { if (!rpcEncoded) mediator = new
     * SOAPRPCLiteralMediatorImpl(this, serverMode); // RPC-literal else mediator =
     * new SOAPRPCEncodedMediatorImpl(this, serverMode); // RPC-encoded }
     * return mediator; }
     */

    /**
     * Get the operation signature from the WSDL operation
     */
    public List<?> getOperationSignature() {
        if (signature == null) {
            signature = new ArrayList<Object>();

            Operation operation = bindingOperation.getOperation();
            if (operation == null) {
                return signature;
            }

            final Input input = operation.getInput();
            if (input == null) {
                return signature;
            }

            String sstyle = getStyle();

            if ("rpc".equals(sstyle)) {
                Collection partNames = input.getMessage().getParts().values();
                for (Iterator i = partNames.iterator(); i.hasNext();) {
                    Part part = (Part) i.next();
                    signature.add(part.getName());
                }
            } else {
                /*
                 * WS-I Basic Profile 1.1 4.7.6 Operation Signatures Definition: operation signature
                 * 
                 * The profile defines the "operation signature" to be the fully qualified name of the child element of
                 * SOAP body of the SOAP input
                 * message described by an operation in a WSDL binding.
                 * 
                 * In the case of rpc-literal binding, the operation name is used as a wrapper for the part accessors.
                 * In the document-literal case, designed so that they meet this requirement.
                 *
                 * An endpoint that supports multiple operations must unambiguously identify the operation being
                 * invoked based on the input message
                 * that it receives. This is only possible if all the operations specified in the wsdl:binding
                 * associated with an endpoint have a
                 * unique operation signature.
                 * 
                 * R2710 The operations in a wsdl:binding in a DESCRIPTION MUST result in operation signatures that are
                 * different from one another.
                 */
                List<String> bodyParts = getSOAPBodyParts(true);

                Collection<?> parts = input.getMessage().getParts().values();
                // Exclude the parts to be transmitted in SOAP header
                if (bodyParts == null) {
                    parts.removeAll(getInputHeaderParts());
                }
                for (Iterator i = parts.iterator(); i.hasNext();) {
                    Part part = (Part) i.next();
                    if (bodyParts == null) {
                        // All parts
                        QName elementName = part.getElementName();
                        if (elementName == null) {
                            elementName = new QName("", part.getName());
                            // TODO: [rfeng] throw new
                            // ServiceRuntimeException("Message part for
                            // document style must refer to an XSD element
                            // using a QName: " + part);
                        }
                        signature.add(elementName);
                    } else {
                        // "parts" in soap:body
                        if (bodyParts.contains(part.getName())) {
                            QName elementName = part.getElementName();
                            if (elementName == null) {
                                elementName = new QName("", part.getName());
                                // TODO: [rfeng] throw new
                                // ServiceRuntimeException("Message part for
                                // document style must refer to an XSD
                                // element using a QName: " + part);
                            }
                            signature.add(elementName);
                        }

                    }
                }
            }
        }
        return signature;
    }

    public Message getMessage(boolean isInput) {
        Operation operation = bindingOperation.getOperation();
        if (operation == null) {
            return null;
        }

        if (isInput) {
            final Input input = operation.getInput();
            return input == null ? null : input.getMessage();
        } else {
            final Output output = operation.getOutput();
            return output == null ? null : output.getMessage();
        }
    }

    public Part getInputPart(int index) {
        Part part = null;
        Message message = getMessage(true);
        if (message == null) {
            return part;
        }

        List parts = message.getOrderedParts(null);
        return (Part) parts.get(index);

    }

    public Part getOutputPart(int index) {
        Part part = null;
        Message message = getMessage(false);
        if (message == null) {
            return part;
        }

        List parts = message.getOrderedParts(null);
        return (Part) parts.get(index);

    }

    /**
     * Get a list of indexes for each part in the SOAP body
     *
     * @param isInput
     */
    public List<Integer> getBodyPartIndexes(boolean isInput) {
        List<Integer> indexes = new ArrayList<Integer>();

        Message message = getMessage(isInput);
        if (message == null) {
            return indexes;
        }

        List<String> bodyParts = getSOAPBodyParts(isInput);
        List parts = message.getOrderedParts(null);
        Set headerParts = isInput ? getInputHeaderParts() : getOutputHeaderParts();

        int index = 0;
        for (Iterator i = parts.iterator(); i.hasNext(); index++) {
            Part part = (Part) i.next();
            if (headerParts.contains(part)) {
                continue;
            }
            if (bodyParts == null) {
                // All parts
                indexes.add(index);
            } else {
                // "parts" in soap:body
                if (bodyParts.contains(part.getName())) {
                    indexes.add(index);
                }
            }
        }
        return indexes;
    }

    /**
     * Get the corresponding index for a part in the SOAP header by element name
     *
     * @param elementName
     * @param isInput
     */
    public int getHeaderPartIndex(QName elementName, boolean isInput) {

        Message message = getMessage(isInput);
        if (message == null) {
            return -1;
        }

        List parts = message.getOrderedParts(null);
        Set headerParts = isInput ? getInputHeaderParts() : getOutputHeaderParts();

        int index = 0;
        for (Iterator i = parts.iterator(); i.hasNext(); index++) {
            Part part = (Part) i.next();
            // Test if the part is in header section
            if (headerParts.contains(part) && elementName.equals(part.getElementName())) {
                return index;
            }
        }
        return -1;
    }

    public BindingOperation getBindingOperation() {
        return bindingOperation;
    }

}
