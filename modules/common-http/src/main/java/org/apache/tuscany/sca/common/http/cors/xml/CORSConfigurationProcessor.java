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

package org.apache.tuscany.sca.common.http.cors.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.common.http.cors.CORSConfiguration;
import org.apache.tuscany.sca.common.http.cors.CORSConfigurationFactory;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXAttributeProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;

/**
 * CORS Configuration Artifact processor
 * @version $Rev$ $Date$
 */
public class CORSConfigurationProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<CORSConfiguration> {
    
    private static final QName CORS_QNAME = new QName(Base.SCA11_TUSCANY_NS, "corsConfiguration");
    
    private static final QName ALLOW_CREDENTIALS_QNAME = new QName(Base.SCA11_TUSCANY_NS, "allowCredentials");
    private static final QName MAX_AGE_QNAME = new QName(Base.SCA11_TUSCANY_NS, "maxAge");

    private static final QName ALLOW_ORIGINS_QNAME = new QName(Base.SCA11_TUSCANY_NS, "allowOrigins");
    private static final QName ORIGIN_QNAME = new QName(Base.SCA11_TUSCANY_NS, "origin");
    
    private static final QName ALLOW_METHODS_QNAME = new QName(Base.SCA11_TUSCANY_NS, "allowMethods");
    private static final QName METHOD_QNAME = new QName(Base.SCA11_TUSCANY_NS, "method");

    private static final QName ALLOW_HEADERS_QNAME = new QName(Base.SCA11_TUSCANY_NS, "allowHeaders");
    private static final QName EXPOSE_HEADERS_QNAME = new QName(Base.SCA11_TUSCANY_NS, "exposeHeaders");
    private static final QName HEADER_QNAME = new QName(Base.SCA11_TUSCANY_NS, "header");
    

    
    private StAXArtifactProcessor<Object> extensionProcessor;
    private CORSConfigurationFactory corsConfigurationFactory;

    public CORSConfigurationProcessor(ExtensionPointRegistry extensionPoints,
                                StAXArtifactProcessor<Object> extensionProcessor,
                                StAXAttributeProcessor<Object> extensionAttributeProcessor) {
        
        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        this.corsConfigurationFactory = modelFactories.getFactory(CORSConfigurationFactory.class);
        
        this.extensionProcessor = (StAXArtifactProcessor<Object>)extensionProcessor;
    }

    
    @Override
    public QName getArtifactType() {
        return CORS_QNAME;
    }

    @Override
    public Class<CORSConfiguration> getModelType() {
        return CORSConfiguration.class;
    }

    @Override
    public CORSConfiguration read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException, XMLStreamException {

        CORSConfiguration corsConfiguration = corsConfigurationFactory.createCORSConfiguration();
        
        /**
         *  <corsConfiguration>
         *       <allowCredentials>true</allowCredentials>
         *
         *       <maxAge>100</maxAge>
         *
         *       <allowOrigins>
         *          <origin>http://www.sfly.com</origin>
         *       </allowOrigins>
         *
         *       <allowMethods>
         *          <method>PUT</method>
         *          <method>POST</method>
         *      </allowMethods>
         *
         *       <allowHeaders>
         *          <header>X-custom-1</header>
         *          <header>X-custom-2</header>
         *       </allowHeaders>
         *
         *       <exposeHeaders>
         *          <header>X-custom-1</header>
         *          <header>X-custom-2</header>
         *       </exposeHeaders>
         *
         *    </corsConfiguration>
         **/
        
        HeaderElementType headerType = null;
        
        while(reader.hasNext()) {
            QName elementName = null;
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT:
                    elementName = reader.getName();

                   if (ALLOW_CREDENTIALS_QNAME.equals(elementName)) {
                        String value = reader.getElementText();
                        corsConfiguration.setAllowCredentials(Boolean.parseBoolean(value));
                    } else if (MAX_AGE_QNAME.equals(elementName)) {
                        String value = reader.getElementText();
                        corsConfiguration.setMaxAge(Integer.parseInt(value));
                    } else if (ORIGIN_QNAME.equals(elementName)) {
                        String value = reader.getElementText();
                        corsConfiguration.getAllowOrigins().add(value);
                    } else if (METHOD_QNAME.equals(elementName)) {
                        String value = reader.getElementText();
                        corsConfiguration.getAllowMethods().add(value);
                    } else if (ALLOW_HEADERS_QNAME.equals(elementName)) {
                        headerType = HeaderElementType.ALLOWHEADERS;
                    } else if (EXPOSE_HEADERS_QNAME.equals(elementName)) {
                        headerType = HeaderElementType.EXPOSEHEADERS;
                    } else if (HEADER_QNAME.equals(elementName)) {
                        if(headerType != null) {
                            String value = reader.getElementText();
                            if(headerType == HeaderElementType.ALLOWHEADERS) {
                                corsConfiguration.getAllowHeaders().add(value);
                            } else {
                                corsConfiguration.getExposeHeaders().add(value);
                            }
                        }
                    }
                   
                   break;

                case END_ELEMENT:
                    elementName = reader.getName();
                    if(CORS_QNAME.equals(elementName)) {
                        return corsConfiguration;
                    }
                    break;
            }



            // Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }

        return corsConfiguration;
    }

    @Override
    public void write(CORSConfiguration model, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException, XMLStreamException {

        writeStart(writer, CORS_QNAME.getNamespaceURI(), CORS_QNAME.getLocalPart());

        // Write allowCredentials
        writeStart(writer, ALLOW_CREDENTIALS_QNAME.getNamespaceURI(), ALLOW_CREDENTIALS_QNAME.getLocalPart());
        writer.writeCharacters(Boolean.toString(model.isAllowCredentials()));
        writeEnd(writer);
        
        // Write maxAge
        writeStart(writer, MAX_AGE_QNAME.getNamespaceURI(), MAX_AGE_QNAME.getLocalPart());
        writer.writeCharacters(Integer.toString(model.getMaxAge()));
        writeEnd(writer);
        
        // Allow origins
        if(model.getAllowOrigins() != null && model.getAllowOrigins().isEmpty() == false) {
            writeStart(writer, ALLOW_ORIGINS_QNAME.getNamespaceURI(), ALLOW_ORIGINS_QNAME.getLocalPart());
            for(String origin : model.getAllowOrigins()) {
                writeStart(writer, ORIGIN_QNAME.getNamespaceURI(), ORIGIN_QNAME.getLocalPart());
                writer.writeCharacters(origin);
                writeEnd(writer);
            }
            writeEnd(writer);
        }

        // Allow methods
        if(model.getAllowMethods() != null && model.getAllowMethods().isEmpty() == false) {
            writeStart(writer, ALLOW_METHODS_QNAME.getNamespaceURI(), ALLOW_METHODS_QNAME.getLocalPart());
            for(String method : model.getAllowMethods()) {
                writeStart(writer, METHOD_QNAME.getNamespaceURI(), METHOD_QNAME.getLocalPart());
                writer.writeCharacters(method);
                writeEnd(writer);
            }
            writeEnd(writer);
        }


        // Allow headers
        if(model.getAllowHeaders() != null && model.getAllowHeaders().isEmpty() == false) {
            writeStart(writer, ALLOW_HEADERS_QNAME.getNamespaceURI(), ALLOW_HEADERS_QNAME.getLocalPart());
            for(String header : model.getAllowHeaders()) {
                writeStart(writer, HEADER_QNAME.getNamespaceURI(), HEADER_QNAME.getLocalPart());
                writer.writeCharacters(header);
                writeEnd(writer);
            }
            writeEnd(writer);
        }
        

        // Exposed headers
        if(model.getExposeHeaders() != null && model.getExposeHeaders().isEmpty() == false) {
            writeStart(writer, EXPOSE_HEADERS_QNAME.getNamespaceURI(), EXPOSE_HEADERS_QNAME.getLocalPart());
            for(String header : model.getExposeHeaders()) {
                writeStart(writer, HEADER_QNAME.getNamespaceURI(), HEADER_QNAME.getLocalPart());
                writer.writeCharacters(header);
                writeEnd(writer);
            }
            writeEnd(writer);
        }
        writeEnd(writer);
    }

    @Override
    public void resolve(CORSConfiguration model, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
        // Should not need to do anything here for now...
    }
    
    
    enum HeaderElementType {
        ALLOWHEADERS,
        EXPOSEHEADERS
    }
    

}
