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

import java.lang.annotation.ElementType;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.core.marshaller.extensions.AbstractIFProviderDefinitionMarshaller;
import org.apache.tuscany.core.marshaller.extensions.java.PojoPhysicalComponentDefinitionMarshaller;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSiteMapping;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSource;
import org.apache.tuscany.core.model.physical.instancefactory.MemberSite;
import org.apache.tuscany.core.model.physical.instancefactory.ReflectiveIFProviderDefinition;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSource.ValueSourceType;
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
    public static final String CDI_SOURCE = "cdiSOurce";
    
    // Injection site
    public static final String INJECTION_SITE = "injectionSite";
    
    // Property
    public static final String PROPERTY = "property";
    
    // Injection site type
    public static final String TYPE = "type";
    
    // Injection site class
    public static final String ELEMENT_TYPE = "elementType";
    
    // Injection site URI
    public static final String NAME = "name";
    
    // Property value
    public static final String VALUE = "value";
    
    // Injection site name
    public static final String PHYSICAL_NAME = "physicalName";

    // QName for the root element
    private static final QName QNAME =
        new QName(REFLECTIVE_NS, PojoPhysicalComponentDefinitionMarshaller.INSTANCE_FACTORY_PROVIDER, REFLECTIVE_PREFIX);

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
                
            } else if(CDI_SOURCE.equals(name)) {
                
                InjectionSource injectionSource = new InjectionSource();
                injectionSource.setName(reader.getAttributeValue(null, NAME));
                injectionSource.setValueType(ValueSourceType.valueOf(reader.getAttributeValue(null, ELEMENT_TYPE)));
                modelObject.addCdiSource(injectionSource);
                
            } else if(INJECTION_SITE.equals(name)) {
                
                InjectionSiteMapping injectionSite = new InjectionSiteMapping();

                InjectionSource injectionSource = new InjectionSource();
                injectionSource.setName(reader.getAttributeValue(null, NAME));
                injectionSource.setValueType(ValueSourceType.valueOf(reader.getAttributeValue(null, TYPE)));
                
                MemberSite memberSite = new MemberSite();
                memberSite.setElementType(ElementType.valueOf(reader.getAttributeValue(null, ELEMENT_TYPE)));
                memberSite.setName(reader.getAttributeValue(null, PHYSICAL_NAME));
                
                injectionSite.setSite(memberSite);
                injectionSite.setSource(injectionSource);
                
                modelObject.addInjectionSite(injectionSite);
                
            } else if(PROPERTY.equals(name)) {
                
                InjectionSource injectionSource = new InjectionSource();
                injectionSource.setName(reader.getAttributeValue(null, NAME));
                injectionSource.setValueType(ValueSourceType.PROPERTY);
                modelObject.addPropertValue(injectionSource, reader.getAttributeValue(null, VALUE));
                
            }
            
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
            
            for(InjectionSource cdiSource : modelObject.getCdiSources()) {
                writer.writeStartElement(QNAME.getPrefix(), CDI_SOURCE, QNAME.getNamespaceURI());
                writer.writeAttribute(NAME, cdiSource.getName());
                writer.writeAttribute(TYPE, cdiSource.getValueType().name());
                writer.writeEndElement();
            }
            
            for(InjectionSiteMapping injectionSite : modelObject.getInjectionSites()) {
                
                MemberSite memberSite = injectionSite.getSite();
                InjectionSource source = injectionSite.getSource();
                
                writer.writeStartElement(QNAME.getPrefix(), INJECTION_SITE, QNAME.getNamespaceURI());
                writer.writeAttribute(TYPE, source.getValueType().name());
                writer.writeAttribute(ELEMENT_TYPE, memberSite.getElementType().name());
                writer.writeAttribute(NAME, source.getName());
                writer.writeAttribute(PHYSICAL_NAME, memberSite.getName());
                writer.writeEndElement();
            }
            
            Map<InjectionSource, String> propertyValues = modelObject.getPropertyValues();
            for(InjectionSource propertySource : propertyValues.keySet()) {
                writer.writeStartElement(QNAME.getPrefix(), PROPERTY, QNAME.getNamespaceURI());
                writer.writeAttribute(NAME, propertySource.getName());
                writer.writeAttribute(TYPE, propertyValues.get(propertySource));
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
