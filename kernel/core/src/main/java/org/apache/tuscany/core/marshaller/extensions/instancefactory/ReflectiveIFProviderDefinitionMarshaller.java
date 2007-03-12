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

package org.apache.tuscany.core.marshaller.extensions.instancefactory;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.core.marshaller.extensions.AbstractIFProviderDefinitionMarshaller;
import org.apache.tuscany.core.marshaller.extensions.AbstractPhysicalComponentDefinitionMarshaller;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSite;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSiteType;
import org.apache.tuscany.core.model.physical.instancefactory.ReflectiveIFProviderDefinition;
import org.apache.tuscany.spi.marshaller.MarshalException;

/**
 * Byte code instance factory definition marshaller.
 * 
 * @version $Revision$ $Date$
 */
public class ReflectiveIFProviderDefinitionMarshaller extends
    AbstractIFProviderDefinitionMarshaller<ReflectiveIFProviderDefinition> {
    
    // Byte code extension NS
    public static final String REFLECTIVE_NS = "http://tuscany.apache.org/xmlns/marshaller/reflect/1.0-SNAPSHOT";

    // Byte code prefix
    public static final String REFLECTIVE_PREFIX = "reflect";
    
    // Implementation class
    public static final String IMPL_CLASS = "implementationClass";
    
    // Init method
    public static final String INIT_METHOD = "initMethod";
    
    // Destroy method
    public static final String DESTROY_METHOD = "destroyMethod";
    
    // Constructor argument
    public static final String CONSTRUCTOR_ARGUMENT = "constructorArgument";
    
    // Constructor injection name
    public static final String CONSTRUCTOR_INJECTION_NAME = "constructorInjectionName";
    
    // Injection site
    public static final String INJECTION_SITE = "injectionSite";
    
    // Injection site type
    public static final String INJECTION_SITE_TYPE = "type";
    
    // Injection site class
    public static final String INJECTION_SITE_CLASS = "class";
    
    // Injection site URI
    public static final String INJECTION_SITE_URI = "uri";

    // QName for the root element
    private static final QName QNAME =
        new QName(REFLECTIVE_NS, AbstractPhysicalComponentDefinitionMarshaller.INSTANCE_FACTORY_PROVIDER, REFLECTIVE_PREFIX);

    @Override
    protected ReflectiveIFProviderDefinition getConcreteModelObject() {
        return new ReflectiveIFProviderDefinition();
    }

    @Override
    protected void handleExtension(ReflectiveIFProviderDefinition modelObject, XMLStreamReader reader)
        throws MarshalException {
        
        try {
            String name = reader.getName().getLocalPart();
            if(IMPL_CLASS.equals(name)) {
                modelObject.setImplementationClass(reader.getElementText());
            } else if(INIT_METHOD.equals(name)) {
                modelObject.setInitMethod(reader.getElementText());
            } else if(DESTROY_METHOD.equals(name)) {
                modelObject.setDestroyMethod(reader.getElementText());
            } else if(CONSTRUCTOR_ARGUMENT.equals(name)) {
                modelObject.addConstructorArgument(reader.getElementText());
            } else if(CONSTRUCTOR_INJECTION_NAME.equals(name)) {
                modelObject.addConstructorNames(new URI(reader.getElementText()));
            } else if(INJECTION_SITE.equals(name)) {
                InjectionSite injectionSite = new InjectionSite();
                injectionSite.setType(InjectionSiteType.valueOf(reader.getAttributeValue(null, INJECTION_SITE_TYPE)));
                injectionSite.setInjectionClass(reader.getAttributeValue(null, INJECTION_SITE_CLASS));
                injectionSite.setUri(new URI(reader.getAttributeValue(null, INJECTION_SITE_URI)));
                modelObject.addInjectionSite(injectionSite);
            }
        } catch(URISyntaxException ex) {
            throw new MarshalException(ex);
        } catch (XMLStreamException ex) {
            throw new MarshalException(ex);
        }

    }

    @Override
    protected void handleExtension(ReflectiveIFProviderDefinition modelObject, XMLStreamWriter writer)
        throws MarshalException {
        
        try {
            
            writer.writeStartElement(QNAME.getPrefix(), IMPL_CLASS, QNAME.getNamespaceURI());
            writer.writeCharacters(modelObject.getImplementationClass());
            writer.writeEndElement();
            
            String initMethod = modelObject.getInitMethod();
            if(initMethod != null) {
                writer.writeStartElement(QNAME.getPrefix(), INIT_METHOD, QNAME.getNamespaceURI());
                writer.writeCharacters(initMethod);
                writer.writeEndElement();
            }
            
            String destroyMethod = modelObject.getDestroyMethod();
            if(destroyMethod != null) {
                writer.writeStartElement(QNAME.getPrefix(), DESTROY_METHOD, QNAME.getNamespaceURI());
                writer.writeCharacters(modelObject.getDestroyMethod());
                writer.writeEndElement();
            }
            
            for(String constructorArgument : modelObject.getConstructorArguments()) {
                writer.writeStartElement(QNAME.getPrefix(), CONSTRUCTOR_ARGUMENT, QNAME.getNamespaceURI());
                writer.writeCharacters(constructorArgument);
                writer.writeEndElement();
            }
            
            for(URI conctructorName : modelObject.getConstructorNames()) {
                writer.writeStartElement(QNAME.getPrefix(), CONSTRUCTOR_INJECTION_NAME, QNAME.getNamespaceURI());
                writer.writeCharacters(conctructorName.toASCIIString());
                writer.writeEndElement();
            }
            
            for(InjectionSite injectionSite : modelObject.getInjectionSites()) {
                writer.writeStartElement(QNAME.getPrefix(), INJECTION_SITE, QNAME.getNamespaceURI());
                writer.writeAttribute(INJECTION_SITE_TYPE, injectionSite.getType().name());
                writer.writeAttribute(INJECTION_SITE_CLASS, injectionSite.getInjectionClass());
                writer.writeAttribute(INJECTION_SITE_URI, injectionSite.getUri().toASCIIString());
                writer.writeEndElement();
            }
            
        } catch (XMLStreamException ex) {
            throw new MarshalException(ex);
        }
    }

    @Override
    protected QName getModelObjectQName() {
        return QNAME;
    }

    @Override
    protected Class<ReflectiveIFProviderDefinition> getModelObjectType() {
        return ReflectiveIFProviderDefinition.class;
    }

}
