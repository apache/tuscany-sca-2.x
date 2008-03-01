package org.apache.tuscany.sca.tools.maven.plugin.shade.transformer;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.codehaus.mojo.shade.resource.AppendingTransformer;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

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

/**
 * Shade transformer for appending definitions.xml file from the various
 * Tuscany modules and wrapping the aggregation with tuscany:definitions element
 */
public class SCADefinitionsAppendingTransformer extends AppendingTransformer {
    private Document resultDoc = null;
    private static final String scaDefinitionsFilePath = "META-INF/services/definitions.xml";
    private static final String TUSCANY_NS = "http://tuscany.apache.org/xmlns/sca/1.0";
    private static final String DEFINITIONS = "definitions";
    private static final String TUSCANY_PREFIX = "tuscany";
    private String resource = scaDefinitionsFilePath;

    public boolean canTransformResource(String resource) {
        if ( resource != null && resource.equals(scaDefinitionsFilePath) ) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasTransformedResource() {
        return resultDoc != null;
    }

    public void modifyOutputStream(JarOutputStream jos) throws IOException {
        jos.putNextEntry(new JarEntry(scaDefinitionsFilePath));
        new XMLOutputter(Format.getPrettyFormat()).output(resultDoc, jos);
        resultDoc = null;
        
    }

    public void processResource(InputStream is) throws IOException {
        Document resource;
        try {
            resource = new SAXBuilder().build(is);
        } catch (JDOMException e) {
            throw new RuntimeException(e);
        }

        if (resultDoc == null) {
            resultDoc = new Document();
            resultDoc.setRootElement(new Element(DEFINITIONS, TUSCANY_PREFIX, TUSCANY_NS));
        } 
            
        if ( resource != null ) {
            Element root = resource.getRootElement();
            resultDoc.getRootElement().addContent(root.detach());
        }
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

}
