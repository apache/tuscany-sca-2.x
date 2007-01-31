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
package org.apache.tuscany.tools.java2wsdl.generate;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.axiom.om.OMElement;
import org.apache.ws.java2wsdl.Java2WSDLConstants;

public class TuscanyJava2WSDLBuilder implements Java2WSDLConstants {

    private OutputStream out;
    private String className;
    private ClassLoader classLoader;
    private String wsdlPrefix = "wsdl";

    // these apply for the WSDL
    private GenerationParameters generationParams;

    private OMElement wsdlDocument = null;

    public String getWsdlPrefix() {
        return wsdlPrefix;
    }

    public void setWsdlPrefix(String wsdlPrefix) {
        this.wsdlPrefix = wsdlPrefix;
    }

    public TuscanyJava2WSDLBuilder(GenerationParameters genParams) {
        this.generationParams = genParams;
    }

    /**
     * Externally visible generator method
     * 
     * @throws Exception
     */
    public void buildWSDL() throws Exception {
        ArrayList excludeOpeartion = new ArrayList();
        excludeOpeartion.add("init");
        excludeOpeartion.add("setOperationContext");
        excludeOpeartion.add("destroy");

        TuscanyWSDLTypesGenerator typesGenerator = new TuscanyWSDLTypesGenerator(generationParams);
        typesGenerator.setExcludeMethods(excludeOpeartion);
        Collection schemaCollection = typesGenerator.buildWSDLTypes();

        TuscanyJava2OMBuilder java2OMBuilder =
            new TuscanyJava2OMBuilder(typesGenerator.getMethods(), schemaCollection, typesGenerator
                .getTypeTable(), generationParams);

        wsdlDocument = java2OMBuilder.generateOM();
    }

    public OMElement getWsdlDocument() {
        return wsdlDocument;
    }

    public void setWsdlDocument(OMElement wsdlDocument) {
        this.wsdlDocument = wsdlDocument;
    }
}
