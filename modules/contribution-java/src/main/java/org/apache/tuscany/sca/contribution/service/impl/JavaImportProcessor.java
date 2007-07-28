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

package org.apache.tuscany.sca.contribution.service.impl;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.java.JavaImport;
import org.apache.tuscany.sca.contribution.java.impl.JavaImportImpl;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;

public class JavaImportProcessor  implements StAXArtifactProcessor<JavaImport> {
    private static final String SCA10_NS = "http://www.osoa.org/xmlns/sca/1.0";
    
    private static final QName IMPORT_JAVA = new QName(SCA10_NS, "import.java");

    private static final String PACKAGE = "package";
    private static final String LOCATION = "location";
    
    public JavaImportProcessor() {
        super();
    }
    
    public QName getArtifactType() {
        return IMPORT_JAVA;
    }
    
    public Class<JavaImport> getModelType() {
        return JavaImport.class;
    }

    public JavaImport read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        JavaImport javaImport = new JavaImportImpl();
        QName element = null;
        
        while (reader.hasNext()) {
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT:
                    element = reader.getName();
                    
                    if (IMPORT_JAVA.equals(element)) {
                        String packageName = reader.getAttributeValue(null, PACKAGE);
                        if (packageName == null) {
                            throw new ContributionReadException("Attribute 'package' is missing");
                        }
                        
                        String location = reader.getAttributeValue(null, LOCATION);
                        javaImport.setPackage(packageName);
                        javaImport.setLocation(location);
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (IMPORT_JAVA.equals(reader.getName())) {
                        return javaImport;
                    }
                    break;        
            }
            
            //Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }
        
        return javaImport;
    }

    public void write(JavaImport model, XMLStreamWriter outputSource) throws ContributionWriteException, XMLStreamException {
        // TODO Auto-generated method stub
        
    }


    public void resolve(JavaImport model, ModelResolver resolver) throws ContributionResolveException {
        // TODO Auto-generated method stub
        
    }
}
