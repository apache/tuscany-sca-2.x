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
package org.apache.tuscany.tools.wsdl2java.generate;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis2.util.FileWriter;
import org.apache.axis2.wsdl.codegen.emitter.JavaEmitter;
import org.apache.axis2.wsdl.codegen.writer.InterfaceWriter;
import org.apache.axis2.wsdl.databinding.TypeMapper;
import org.apache.wsdl.MessageReference;
import org.apache.wsdl.WSDLExtensibilityAttribute;
import org.apache.wsdl.WSDLOperation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

class JavaInterfaceEmitter extends JavaEmitter {
    
    private List getParameterElementList(Document doc, MessageReference message) {
        List parameterElementList = new ArrayList();
        
        if (message != null) {

            Object typeMappingObject = this.mapper.getTypeMappingObject(message.getElementQName());
            List typeMappingList;
            if (typeMappingObject instanceof List) {
                typeMappingList = (List)typeMappingObject;
            } else {
                typeMappingList = new ArrayList();
                typeMappingList.add(typeMappingObject);
            }
            
            for (int i=0; i<typeMappingList.size(); i++) {
                Element param = doc.createElement("param");
                parameterElementList.add(param);
                
                String typeMapping = (String)typeMappingList.get(i);
    
                addAttribute(doc, "name", this.mapper.getParameterName(message.getElementQName()), param);
                addAttribute(doc, "type", (typeMapping == null)
                        ? ""
                        : typeMapping, param);
    
                // add an extra attribute to say whether the type mapping is the default
                if (TypeMapper.DEFAULT_CLASS_NAME.equals(typeMapping)) {
                    addAttribute(doc, "default", "yes", param);
                }
    
                addAttribute(doc, "value", null, param);
    
                // add this as a body parameter
                addAttribute(doc, "location", "body", param);
    
                Iterator iter = message.getExtensibilityAttributes().iterator();
    
                while (iter.hasNext()) {
                    WSDLExtensibilityAttribute att = (WSDLExtensibilityAttribute) iter.next();
    
                    addAttribute(doc, att.getKey().getLocalPart(), att.getValue().toString(), param);
                }
            }
        }

        return parameterElementList;
    }
    
    private List getParameterElementList(Document doc, List parameters, String location) {
        List parameterElementList = new ArrayList();

        if ((parameters != null) && !parameters.isEmpty()) {
            int count = parameters.size();

            for (int i = 0; i < count; i++) {
                Element param = doc.createElement("param");
                QName name = (QName) parameters.get(i);

                addAttribute(doc, "name", this.mapper.getParameterName(name), param);

                String typeMapping = this.mapper.getTypeMappingName(name);
                String typeMappingStr = (typeMapping == null)
                        ? ""
                        : typeMapping;

                addAttribute(doc, "type", typeMappingStr, param);
                addAttribute(doc, "location", location, param);
                parameterElementList.add(param);
            }
        }

        return parameterElementList;
    }

    protected Element getInputElement(Document doc, WSDLOperation operation, List headerParameterQNameList) {
        return getElement(doc, "input", operation.getInputMessage(), headerParameterQNameList);
    }
    
    protected Element getOutputElement(Document doc, WSDLOperation operation, List headerParameterQNameList) {
        return getElement(doc, "output", operation.getOutputMessage(), headerParameterQNameList);
    }

    protected Element getElement(Document doc, String elementName, MessageReference message, List headerParameterQNameList) {
        Element element = doc.createElement(elementName);

        List parameterElementList = getParameterElementList(doc, message);
        for (int i = 0; i < parameterElementList.size(); i++) {
            element.appendChild((Element) parameterElementList.get(i));
        }

        List outputElementList = getParameterElementList(doc, headerParameterQNameList, "header");

        for (int i = 0; i < outputElementList.size(); i++) {
            element.appendChild((Element) outputElementList.get(i));
        }

        return element;
    }

    protected void writeInterface(boolean writeDatabinders) throws Exception {
        Document interfaceModel = createDOMDocumentForInterface(writeDatabinders);
        if (!configuration.getOutputLocation().exists()) {
            configuration.getOutputLocation().mkdirs();
        }
        InterfaceWriter interfaceWriter = new InterfaceWriter(this.configuration
            .getOutputLocation(), this.configuration.getOutputLanguage());

        String packageName = interfaceModel.getDocumentElement().getAttribute("package");
        String className = interfaceModel.getDocumentElement().getAttribute("name");

        System.out.println(">>  Generating Java class " + packageName + "." + className);
        File outputFile = FileWriter.createClassFile(this.configuration.getOutputLocation(),
                                                     packageName, className, ".java");
        if (outputFile.exists()) {
            outputFile.delete();
        }

        writeClass(interfaceModel, interfaceWriter);
    }

}
