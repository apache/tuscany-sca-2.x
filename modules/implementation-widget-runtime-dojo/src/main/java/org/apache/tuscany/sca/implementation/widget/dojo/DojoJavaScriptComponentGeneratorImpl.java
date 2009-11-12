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

package org.apache.tuscany.sca.implementation.widget.dojo;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.web.javascript.ComponentJavaScriptGenerator;
import org.apache.tuscany.sca.web.javascript.JavascriptProxyFactory;
import org.apache.tuscany.sca.web.javascript.JavascriptProxyFactoryExtensionPoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DojoJavaScriptComponentGeneratorImpl implements ComponentJavaScriptGenerator {
    private static final QName NAME = new QName("http://tuscany.apache.org/xmlns/sca/1.0", "component.script.generator.dojo");

    private JavascriptProxyFactoryExtensionPoint javascriptProxyFactories;

    public DojoJavaScriptComponentGeneratorImpl(ExtensionPointRegistry extensionPoints) {
        this.javascriptProxyFactories = extensionPoints.getExtensionPoint(JavascriptProxyFactoryExtensionPoint.class);
    }
    
    public QName getQName() {
        return NAME;
    }

    public void generateJavaScriptCode(RuntimeComponent component, PrintWriter pw) throws IOException {
        pw.println();
        pw.println("/* Apache Tuscany SCA Widget header */");
        pw.println();

        Map<String, Boolean> bindingClientProcessed = new HashMap<String, Boolean>();

        for(ComponentReference reference : component.getReferences()) {
            for(Binding binding : reference.getBindings()) {
                JavascriptProxyFactory jsProxyFactory = javascriptProxyFactories.getProxyFactory(binding.getClass());

                String bindingProxyName = jsProxyFactory.getJavascriptProxyFile();
                //check if binding client code was already processed and inject to the generated script
                if(bindingProxyName != null) {
                    Boolean processedFlag = bindingClientProcessed.get(bindingProxyName);
                    if( processedFlag == null || processedFlag.booleanValue() == false) {
                        generateJavaScriptBindingProxy(jsProxyFactory, pw);
                        bindingClientProcessed.put(bindingProxyName, Boolean.TRUE);
                    }
                }

            }
        }
        
        //pw.println("dojo.require(\"/dojo.rpc.JsonService\");");
        //pw.println("dojo.require(\"/tuscany.AtomService\");");

        pw.println();
        pw.println("/* Tuscany Reference/Property injection code */");
        pw.println();

        
        //define tuscany.sca namespace
        generateJavaScriptNamespace(pw);

        pw.println();

        //process properties
        generateJavaScriptPropertyFunction(component, pw);

        pw.println();

        //process references
        generateJavaScriptReferenceFunction(component, javascriptProxyFactories,pw);


        pw.println();
        pw.println("/** End of Apache Tuscany SCA Widget */");
        pw.println();
        pw.flush();
        pw.close();
    }


    /**
     * Retrieve the binding proxy based on the bind name
     * and embedded the JavaScript into this js
     */
    private static void generateJavaScriptBindingProxy(JavascriptProxyFactory javascriptProxyFactory, PrintWriter pw) throws IOException {
        InputStream is = javascriptProxyFactory.getJavascriptProxyFileAsStream();
        if (is != null) {
            int i;
            while ((i = is.read()) != -1) {
                pw.write(i);
            }           
        }
        
        pw.println();
        pw.println();
    }

    /**
     * Generate the tuscany.sca namespace if not yet available
     * @param pw
     * @throws IOException
     */
    private static void generateJavaScriptNamespace(PrintWriter pw) throws IOException {
        pw.println("if (!window.tuscany) { \n" +
                        "window.tuscany = {}; \n" +
                        "}");
        pw.println("var __tus = window.tuscany;");
        
        pw.println("if (!__tus.sca) { \n" +
                        "__tus.sca = {}; \n" +
                        "}");
    }
   

    /**
     * Generate JavaScript code to inject SCA Properties
     * @param pw
     * @throws IOException
     */
    private static void generateJavaScriptPropertyFunction(RuntimeComponent component, PrintWriter pw) throws IOException {        
        pw.println("__tus.sca.propertyMap = {};");
        for(ComponentProperty property : component.getProperties()) {
            String propertyName = property.getName();

            pw.println("__tus.sca.propertyMap." + propertyName + " = new String(\"" + getPropertyValue(property) + "\");");
        }
        
        pw.println("__tus.sca.Property = function (name) {");
        pw.println("    return __tus.sca.propertyMap[name];");
        pw.println("}");
    }
    
    /**
     * Convert property value to String
     * @param property
     * @return
     */
    private static String getPropertyValue(ComponentProperty property) {
        Document doc = (Document)property.getValue();
        Element rootElement = doc.getDocumentElement();

        String value = null;

        //FIXME : Provide support for isMany and other property types

        if (rootElement.getChildNodes().getLength() > 0) {
            value = rootElement.getChildNodes().item(0).getTextContent();
        }

        return value;
    }


    
    /**
     * Generate JavaScript code to inject SCA References
     * @param pw
     * @throws IOException
     */
    private static void generateJavaScriptReferenceFunction (RuntimeComponent component, JavascriptProxyFactoryExtensionPoint javascriptProxyFactories, PrintWriter pw) throws IOException {
        
        pw.println("__tus.sca.referenceMap = {};");
        for(ComponentReference reference : component.getReferences()) {
            Binding binding = reference.getBindings().get(0);
           
            if (binding != null) {

                String referenceName = reference.getName();
                JavascriptProxyFactory jsProxyFactory = javascriptProxyFactories.getProxyFactory(binding.getClass());
                
                pw.println("__tus.sca.referenceMap." + referenceName + " = new " + jsProxyFactory.createJavascriptReference(reference) + ";");
                
            }
        }
        
        pw.println("__tus.sca.Reference = function (name) {");
        pw.println("    return __tus.sca.referenceMap[name];");
        pw.println("}");
    }
    
}
