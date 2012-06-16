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

package org.apache.tuscany.sca.assembly.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.apache.tuscany.sca.assembly.xml.Constants.SCA11_TUSCANY_NS;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceImpl;
import org.apache.tuscany.sca.interfacedef.impl.OperationImpl;
import org.apache.tuscany.sca.interfacedef.impl.TuscanyInterfaceContractImpl;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * Processor for reading/writing the Tuscany interface model in order to 
 * support distributed interface matching
 *
 */
public class InterfaceContractProcessor extends BaseAssemblyProcessor implements StAXArtifactProcessor<InterfaceContract>, Constants{
    
    String INTERFACE_CONTRACT = "interfaceContract";
    QName INTERFACE_CONTRACT_QNAME = new QName(SCA11_TUSCANY_NS, INTERFACE_CONTRACT);
    String INTERFACE = "interface";
    QName INTERFACE_QNAME = new QName(SCA11_TUSCANY_NS, INTERFACE);
    String CALLBACK_INTERFACE = "callbackInterface";
    QName CALLBACK_INTERFACE_QNAME = new QName(SCA11_TUSCANY_NS, CALLBACK_INTERFACE);
    String OPERATION = "operation";
    QName OPERATION_QNAME = new QName(SCA11_TUSCANY_NS, OPERATION);
    String INPUT = "input"; 
    QName INPUT_QNAME = new QName(SCA11_TUSCANY_NS, INPUT);
    String OUTPUT = "output";
    QName OUTPUT_QNAME = new QName(SCA11_TUSCANY_NS, OUTPUT);
    String FAULT = "fault";
    QName FAULT_QNAME = new QName(SCA11_TUSCANY_NS, FAULT);
    String DATATYPE = "dataType";
    QName DATATYPE_QNAME = new QName(SCA11_TUSCANY_NS, DATATYPE);
    String GENERIC = "generic";
    QName GENERIC_QNAME = new QName(SCA11_TUSCANY_NS, GENERIC);
    String LOGICAL_COLLECTION = "logicalCollection";
    QName LOGICAL_COLLECTION_QNAME = new QName(SCA11_TUSCANY_NS, LOGICAL_COLLECTION);
    String LOGICAL_XMLTYPE = "logicalXMLType";
    QName LOGICAL_XMLTYPE_QNAME = new QName(SCA11_TUSCANY_NS, LOGICAL_XMLTYPE);
    String LOGICAL_TYPE = "logicalType";
    QName LOGICAL_TYPE_QNAME = new QName(SCA11_TUSCANY_NS, LOGICAL_TYPE);
    String PHYSICAL = "physical";
    QName PHYSICAL_QNAME = new QName(SCA11_TUSCANY_NS, PHYSICAL);
    String XMLTYPE = "xmlType";
    QName XMLTYPE_QNAME = new QName(SCA11_TUSCANY_NS, XMLTYPE);
    
    String NO_TYPE = "NoType";
    
    enum Iof {
        UNSET,
        INPUT,
        OUTPUT,
        FAULT
    }
    
    enum CharacterTarget {
        UNSET,
        GENERIC,
        PHYSICAL,
        XMLTYPE
    }
    
    public InterfaceContractProcessor(ExtensionPointRegistry registry) {
        super(registry.getExtensionPoint(FactoryExtensionPoint.class), 
              null /*registry.getExtensionPoint(StAXArtifactProcessor.class)*/);
    }

    public InterfaceContract read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException, XMLStreamException {
        
        TuscanyInterfaceContractImpl interfaceContract = new TuscanyInterfaceContractImpl();
        
        try {
            InterfaceImpl iface = null;
            QName name = null;
            Operation operation = null;
            List<DataType> inputs = null;
            List<DataType> outputs = null;
            List<DataType> faults = null;
            XMLType logicalXMLType = null;
            
            Iof iof = Iof.UNSET;
            CharacterTarget characterTarget = CharacterTarget.UNSET;
            
            boolean logicalCollection = false;            
          
            DataType dataType = null;
            
            while (reader.hasNext()) {
                int event = reader.getEventType();
                
                switch (event) {
                    case START_ELEMENT:
                        name = reader.getName();
                        if (INTERFACE_CONTRACT_QNAME.equals(name)){
                        } else if (INTERFACE_QNAME.equals(name)){
                            iface = new InterfaceImpl();
                            interfaceContract.setInterface(iface);
                            iface.setRemotable(getBoolean(reader, "isRemotable"));
                        } else if (CALLBACK_INTERFACE_QNAME.equals(name)){
                            iface = new InterfaceImpl();
                            interfaceContract.setCallbackInterface(iface);
                            iface.setRemotable(getBoolean(reader, "isRemotable"));
                        } else if (OPERATION_QNAME.equals(name)) {
                            operation = new OperationImpl();
                            iface.getOperations().add(operation);
                            
                            operation.setName(getString(reader, "name"));
                            operation.setDynamic(getBoolean(reader, "isDynamic"));
                            operation.setNonBlocking(getBoolean(reader, "isNonBlocking"));
                            operation.setInputWrapperStyle(getBoolean(reader, "isInputWrapperStyle"));
                            operation.setOutputWrapperStyle(getBoolean(reader, "isOutputWrapperStyle"));
                            
                            inputs = new ArrayList<DataType>();
                            DataType inputType = new DataTypeImpl<List<DataType>>(null, null);
                            inputType.setLogical(inputs);
                            operation.setInputType(inputType);
                            
                            outputs = new ArrayList<DataType>();
                            DataType outputType = new DataTypeImpl<List<DataType>>(null, null);
                            outputType.setLogical(outputs);
                            operation.setOutputType(outputType);
                            
                            faults = new ArrayList<DataType>();
                            operation.setFaultTypes(faults);
                        } else if (INPUT_QNAME.equals(name)) {
                            iof = Iof.INPUT;
                        } else if (OUTPUT_QNAME.equals(name)) {
                            iof = Iof.OUTPUT;
                        } else if (FAULT_QNAME.equals(name)) {
                            iof = Iof.FAULT;
                        } else if (DATATYPE_QNAME.equals(name)){
                            DataType newDataType = new DataTypeImpl<XMLType>(null, null);
                            newDataType.setDataBinding(getString(reader, "dataBinding"));
                            if (logicalCollection) {
                                dataType.setLogical(newDataType);
                                dataType = newDataType;
                            } else if (iof == Iof.INPUT) {
                                inputs.add(newDataType);
                                dataType = newDataType;
                            } else if (iof == Iof.OUTPUT){
                                outputs.add(newDataType);
                                dataType = newDataType;
                            } else if (iof == Iof.FAULT){
                                faults.add(newDataType);
                                dataType = newDataType;
                            } 
                        } else if (GENERIC_QNAME.equals(name)){
                            characterTarget = CharacterTarget.GENERIC;
                        } else if (PHYSICAL_QNAME.equals(name)){
                            characterTarget = CharacterTarget.PHYSICAL;
                        } else if (LOGICAL_COLLECTION_QNAME.equals(name)){
                            logicalCollection = true;
                        } else if (LOGICAL_XMLTYPE_QNAME.equals(name)){
                            characterTarget = CharacterTarget.XMLTYPE;
                            logicalXMLType = new XMLType(null, null);
                            dataType.setLogical(logicalXMLType);
                        } else if (LOGICAL_TYPE_QNAME.equals(name)){
                            // is this ever used?
                        } else if (XMLTYPE_QNAME.equals(name)){
                            // is this ever used?
                        } else {
                            System.out.println("Unexpected element " + name);
                        }
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        if (characterTarget == CharacterTarget.GENERIC){
                            String generic = reader.getText();
                            // Not sure what to do with this as we may not have the actual type
                        } else if (characterTarget == CharacterTarget.PHYSICAL){
                            String physical = reader.getText();
                            // Not sure what to do with this as we may not have the actual type
                        } else if (characterTarget == CharacterTarget.XMLTYPE) {
                            String xmlType = reader.getText();
                            if (!xmlType.equals(NO_TYPE)){
                                int splitPoint = xmlType.indexOf("}");
                                String namespace = xmlType.substring(1, splitPoint);
                                String localname = xmlType.substring(splitPoint + 1);
                                QName typeName = new QName(namespace, localname);
                                logicalXMLType.setTypeName(typeName);
                            }
                        }
                        characterTarget = CharacterTarget.UNSET;
                        break;
                    case END_ELEMENT:
                        name = reader.getName();
                        if (INPUT_QNAME.equals(name) ||
                            OUTPUT_QNAME.equals(name) ||
                            FAULT_QNAME.equals(name)) {
                            iof = Iof.UNSET;
                        } else if (LOGICAL_COLLECTION_QNAME.equals(name)) {
                            logicalCollection = false;
                        }
                }
    
                // Read the next element
                if (reader.hasNext()) {
                    reader.next();
                }
            }
        } catch (XMLStreamException e) {
            ContributionReadException ex = new ContributionReadException(e);
            //error(monitor, "XMLStreamException", reader, ex);
        }                    
        
        return interfaceContract;
    }

    public void write(InterfaceContract interfaceContract, XMLStreamWriter writer, ProcessorContext context)
        throws ContributionWriteException, XMLStreamException {

        if (interfaceContract == null || interfaceContract.getInterface() == null) {
            return;
        }
        
        writer.writeStartElement(Constants.SCA11_TUSCANY_NS, INTERFACE_CONTRACT);
        writer.writeStartElement(Constants.SCA11_TUSCANY_NS, INTERFACE);
        writeInterface(interfaceContract.getInterface(), writer, context);
        
        if (interfaceContract.getCallbackInterface() != null){
            writer.writeStartElement(Constants.SCA11_TUSCANY_NS, CALLBACK_INTERFACE);
            writeInterface(interfaceContract.getCallbackInterface(), writer, context);
        }
        writer.writeEndElement();

    }

    private void writeInterface(Interface iface, XMLStreamWriter writer, ProcessorContext context) throws XMLStreamException {

        
        writer.writeAttribute("isRemotable", String.valueOf(iface.isRemotable()));
       
        for (Operation operation : iface.getOperations()){
            writer.writeStartElement(Constants.SCA11_TUSCANY_NS, OPERATION);
            writer.writeAttribute("name", operation.getName());
            writer.writeAttribute("isDynamic", String.valueOf(operation.isDynamic()));
            writer.writeAttribute("isNonBlocking", String.valueOf(operation.isNonBlocking()));
            writer.writeAttribute("isInputWrapperStyle", String.valueOf(operation.isInputWrapperStyle()));
            writer.writeAttribute("isOutputWrapperStyle", String.valueOf(operation.isOutputWrapperStyle()));

            List<DataType> outputTypes = operation.getOutputType().getLogical();
            List<DataType> inputTypes = operation.getInputType().getLogical();
            List<DataType> faultTypes = operation.getFaultTypes();

            if (operation.isInputWrapperStyle() && operation.getInputWrapper() != null) {
                inputTypes = operation.getInputWrapper().getUnwrappedType().getLogical();
            }
            if (operation.isOutputWrapperStyle() && operation.getOutputWrapper() != null) {
                outputTypes = operation.getOutputWrapper().getUnwrappedType().getLogical();
            }
          
            writer.writeStartElement(Constants.SCA11_TUSCANY_NS, INPUT);
            writeDataTypes(inputTypes, writer);
            writer.writeEndElement();
            
            writer.writeStartElement(Constants.SCA11_TUSCANY_NS, OUTPUT);
            writeDataTypes(outputTypes, writer);
            writer.writeEndElement();
            
            writer.writeStartElement(Constants.SCA11_TUSCANY_NS, FAULT);
            writeDataTypes(faultTypes, writer);
            writer.writeEndElement();

            writer.writeEndElement(); 
        }
        writer.writeEndElement();        
    }
    
    private void writeDataTypes(List<DataType> dataTypes, XMLStreamWriter writer) throws XMLStreamException {
        for(DataType dataType : dataTypes){
            writeDataType(dataType, writer);
        }
    }
    
    private void writeDataType(DataType<?> dataType, XMLStreamWriter writer)  throws XMLStreamException {
        writer.writeStartElement(Constants.SCA11_TUSCANY_NS, DATATYPE);
        if (dataType.getDataBinding() != null){
            writer.writeAttribute("dataBinding", dataType.getDataBinding());
        }
       
        if (dataType.getGenericType() != null){
            writer.writeStartElement(Constants.SCA11_TUSCANY_NS, GENERIC);
            writer.writeCharacters(dataType.getGenericType().toString());
            writer.writeEndElement();  
        }
        
        if (dataType.getLogical() instanceof DataType){
            writer.writeStartElement(Constants.SCA11_TUSCANY_NS, LOGICAL_COLLECTION);
            writeDataType((DataType<?>)dataType.getLogical(), writer);
        } else if (dataType.getLogical() instanceof XMLType){
            writer.writeStartElement(Constants.SCA11_TUSCANY_NS, LOGICAL_XMLTYPE);
            writer.writeStartElement(Constants.SCA11_TUSCANY_NS, XMLTYPE);
            XMLType xmlType = (XMLType)dataType.getLogical();
            if (xmlType.getTypeName() != null){
                writer.writeCharacters(xmlType.getTypeName().toString());
            } else {
                writer.writeCharacters("NoType");
            }
            writer.writeEndElement();  
        } else {
            writer.writeStartElement(Constants.SCA11_TUSCANY_NS, LOGICAL_TYPE);
            writer.writeCharacters(dataType.getLogical().toString());
        }
        writer.writeEndElement();
       
        if (dataType.getPhysical() != null){
            writer.writeStartElement(Constants.SCA11_TUSCANY_NS, PHYSICAL);
            writer.writeCharacters(dataType.getPhysical().getName());
            writer.writeEndElement();
        }
        
        writer.writeEndElement();

    }

    public void resolve(InterfaceContract interfaceContract, ModelResolver resolver, ProcessorContext context)
        throws ContributionResolveException {
        // do nothing
    }

    public QName getArtifactType() {
        // these internal interface contracts aren't found in the composite file
        return null;
    }

    public Class<InterfaceContract> getModelType() {
        return InterfaceContract.class;
    }

}


