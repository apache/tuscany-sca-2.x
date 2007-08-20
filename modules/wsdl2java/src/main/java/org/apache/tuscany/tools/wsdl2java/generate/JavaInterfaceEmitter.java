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
package org.apache.tuscany.tools.wsdl2java.generate;

import static org.apache.tuscany.tools.wsdl2java.util.XMLNameUtil.getJavaNameFromXMLName;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis2.description.AxisMessage;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.util.FileWriter;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.axis2.wsdl.codegen.CodeGenConfiguration;
import org.apache.axis2.wsdl.codegen.emitter.JavaEmitter;
import org.apache.axis2.wsdl.codegen.writer.InterfaceWriter;
import org.apache.axis2.wsdl.databinding.TypeMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Overrides the Axis2 JavaEmitter to generate unwrapped methods.
 */
public class JavaInterfaceEmitter extends JavaEmitter {

    private CodeGenConfiguration codegenConfiguration;
    private TypeMapper typeMapper;

    @Override
    public void setCodeGenConfiguration(CodeGenConfiguration configuration) {
        super.setCodeGenConfiguration(configuration);
        codegenConfiguration = configuration;
    }

    @Override
    public void setMapper(TypeMapper typeMapper) {
        super.setMapper(typeMapper);
        this.typeMapper = typeMapper;
    }

    private List getParameterElementList(Document doc, AxisMessage message, boolean wrapped) {
        List parameterElementList = new ArrayList();

        if (message != null && message.getElementQName() != null) {

            SDODataBindingTypeMappingEntry typeMappingEntry =
                (SDODataBindingTypeMappingEntry)this.typeMapper.getTypeMappingObject(message.getElementQName());
            List typeMappings;
            if (wrapped) {
                typeMappings = typeMappingEntry.getPropertyClassNames();
            } else {
                typeMappings = new ArrayList();
                typeMappings.add(typeMappingEntry.getClassName());
            }

            for (int i = 0; i < typeMappings.size(); i++) {
                Element param = doc.createElement("param");
                parameterElementList.add(param);

                String typeMapping = (String)typeMappings.get(i);

                addAttribute(doc, "name", this.typeMapper.getParameterName(message.getElementQName()), param);
                addAttribute(doc, "type", (typeMapping == null) ? "" : typeMapping, param);

                // add an extra attribute to say whether the type mapping is the
                // default
                // if (TypeMapper.DEFAULT_CLASS_NAME.equals(typeMapping)) {
                if (typeMapper.getDefaultMappingName().equals(typeMapping)) {

                    addAttribute(doc, "default", "yes", param);
                }

                addAttribute(doc, "value", null, param);

                // add this as a body parameter
                addAttribute(doc, "location", "body", param);

            }
        }

        return parameterElementList;
    }

    @Override
    public List getParameterElementList(Document doc, List parameters, String location) {
        List parameterElementList = new ArrayList();

        if ((parameters != null) && !parameters.isEmpty()) {
            int count = parameters.size();

            for (int i = 0; i < count; i++) {
                Element param = doc.createElement("param");
                QName name = (QName)parameters.get(i);

                addAttribute(doc, "name", this.typeMapper.getParameterName(name), param);

                String typeMapping = this.typeMapper.getTypeMappingName(name);
                String typeMappingStr = (typeMapping == null) ? "" : typeMapping;

                addAttribute(doc, "type", typeMappingStr, param);
                addAttribute(doc, "location", location, param);
                parameterElementList.add(param);
            }
        }

        return parameterElementList;
    }

    protected boolean isWrapped(AxisOperation operation) {
        boolean wrapped = false;

        if (isInputPresentForMEP(operation.getMessageExchangePattern())) {
            QName qname = operation.getMessage(WSDLConstants.MESSAGE_LABEL_IN_VALUE).getElementQName();
            if (qname != null && qname.getLocalPart().equals(operation.getName().getLocalPart())) {

                //
                // Maybe we should be more strict than this but there's no point
                // in ruling out named
                // complex types.
                //
                // wrapped = true;

                // *
                SDODataBindingTypeMappingEntry typeMappingEntry =
                    (SDODataBindingTypeMappingEntry)this.typeMapper.getTypeMappingObject(qname);
                if (typeMappingEntry.isAnonymous()) {
                    wrapped = true;
                }
                // */
            }
        }

        return wrapped;
    }

    private boolean isInputPresentForMEP(String MEP) {
        // TODO: verify if thi is still correct with Axis2 1.2
        return WSDL2Constants.MEP_URI_IN_ONLY.equals(MEP) || WSDL2Constants.MEP_URI_IN_OPTIONAL_OUT.equals(MEP)
            || WSDL2Constants.MEP_URI_OUT_OPTIONAL_IN.equals(MEP)
            || WSDL2Constants.MEP_URI_ROBUST_OUT_ONLY.equals(MEP)
            || WSDL2Constants.MEP_URI_ROBUST_IN_ONLY.equals(MEP)
            || WSDL2Constants.MEP_URI_IN_OUT.equals(MEP)
            ||

            WSDLConstants.WSDL20_2006Constants.MEP_URI_IN_ONLY.equals(MEP)
            || WSDLConstants.WSDL20_2006Constants.MEP_URI_IN_OPTIONAL_OUT.equals(MEP)
            || WSDLConstants.WSDL20_2006Constants.MEP_URI_OUT_OPTIONAL_IN.equals(MEP)
            || WSDLConstants.WSDL20_2006Constants.MEP_URI_ROBUST_OUT_ONLY.equals(MEP)
            || WSDLConstants.WSDL20_2006Constants.MEP_URI_ROBUST_IN_ONLY.equals(MEP)
            || WSDLConstants.WSDL20_2006Constants.MEP_URI_IN_OUT.equals(MEP)
            ||

            WSDLConstants.WSDL_MESSAGE_DIRECTION_IN.endsWith(MEP);
    }

    @Override
    protected Element getInputElement(Document doc, AxisOperation operation, List headerParameterQNameList) {
        return getElement(doc,
                          "input",
                          operation.getMessage(WSDLConstants.MESSAGE_LABEL_IN_VALUE),
                          isWrapped(operation),
                          headerParameterQNameList);
    }

    @Override
    protected Element getOutputElement(Document doc, AxisOperation operation, List headerParameterQNameList) {
        return getElement(doc,
                          "output",
                          operation.getMessage(WSDLConstants.MESSAGE_LABEL_OUT_VALUE),
                          isWrapped(operation),
                          headerParameterQNameList);
    }

    protected Element getElement(Document doc,
                                 String elementName,
                                 AxisMessage message,
                                 boolean wrapped,
                                 List headerParameterQNameList) {
        Element element = doc.createElement(elementName);

        List parameterElementList = getParameterElementList(doc, message, wrapped);
        for (int i = 0; i < parameterElementList.size(); i++) {
            element.appendChild((Element)parameterElementList.get(i));
        }

        List outputElementList = getParameterElementList(doc, headerParameterQNameList, "header");

        for (int i = 0; i < outputElementList.size(); i++) {
            element.appendChild((Element)outputElementList.get(i));
        }

        return element;
    }

    @Override
    protected void writeInterface(boolean writeDatabinders) throws Exception {
        Document interfaceModel = createDOMDocumentForInterface(writeDatabinders);
        if (!codegenConfiguration.getOutputLocation().exists()) {
            codegenConfiguration.getOutputLocation().mkdirs();
        }
        InterfaceWriter interfaceWriter =
            new RemotableInterfaceWritter(this.codegenConfiguration.getOutputLocation(), this.codegenConfiguration
                .getOutputLanguage());

        String packageName = interfaceModel.getDocumentElement().getAttribute("package");
        String className = interfaceModel.getDocumentElement().getAttribute("name");

        System.out.println(">>  Generating Java class " + packageName + "." + className);
        File outputFile =
            FileWriter.createClassFile(this.codegenConfiguration.getOutputLocation(), packageName, className, ".java");
        if (outputFile.exists()) {
            outputFile.delete();
        }

        writeClass(interfaceModel, interfaceWriter);
    }

    @Override
    protected String makeJavaClassName(String word) {
        // return XMLNameUtil.getJavaNameFromXMLName(word, true);
        return getJavaNameFromXMLName(word, true);
    }

    @Override
    protected Element[] getFaultParamElements(Document doc, AxisOperation operation) {
        ArrayList params = new ArrayList();
        ArrayList faultMessages = operation.getFaultMessages();

        if (faultMessages != null && !faultMessages.isEmpty()) {
            Element paramElement;
            AxisMessage msg;
            for (int i = 0; i < faultMessages.size(); i++) {
                paramElement = doc.createElement("param");
                msg = (AxisMessage)faultMessages.get(i);
                String msgClassName = WSDL2JavaGenerator.normalizeClassName(msg.getName());
                addAttribute(doc, "name", msgClassName, paramElement);
                params.add(paramElement);
            }

            return (Element[])params.toArray(new Element[params.size()]);
        } else {
            return new Element[] {};// return empty array
        }
    }
}
