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
package org.apache.tuscany.sca.implementation.java.injection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.context.PropertyValueFactory;
import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.core.factory.ObjectFactory;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.databinding.SimpleTypeMapper;
import org.apache.tuscany.sca.databinding.impl.DOMHelper;
import org.apache.tuscany.sca.databinding.impl.SimpleTypeMapperImpl;
import org.apache.tuscany.sca.databinding.xml.DOMDataBinding;
import org.apache.tuscany.sca.implementation.java.impl.JavaElementImpl;
import org.apache.tuscany.sca.implementation.java.introspect.impl.JavaIntrospectionHelper;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.TypeInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @version $Rev$ $Date$
 */
public class JavaPropertyValueObjectFactory implements PropertyValueFactory {
    private Mediator mediator = null;
    private boolean isSimpleType;

    public JavaPropertyValueObjectFactory(Mediator mediator) {
        this.mediator = mediator;
    }
    
    public ObjectFactory createValueFactory(Property property, Object propertyValue, JavaElementImpl javaElement) {
        isSimpleType = isSimpleType(property);
        Document doc = (Document)propertyValue;
        Class javaType = JavaIntrospectionHelper.getBaseType(javaElement.getType(), javaElement.getGenericType());
        Element rootElement = doc.getDocumentElement();
        if (property.isMany()) {
            if (isSimpleType) {
                String value = "";
                if (rootElement.getChildNodes().getLength() > 0) {
                    value = rootElement.getChildNodes().item(0).getTextContent();
                }
                List<String> values = getSimplePropertyValues(value, javaType);
                if ( javaElement.getType().isArray() ) {
                    return new ArrayObjectFactoryImpl(property, values, isSimpleType, javaType);
                } else {
                    return new ListObjectFactoryImpl(property, values, isSimpleType, javaType);
                }
            } else {
                if ( javaElement.getType().isArray() ) {
                    return new ArrayObjectFactoryImpl(property, getComplexPropertyValues(doc), isSimpleType, javaType);
                } else {
                    return new ListObjectFactoryImpl(property, getComplexPropertyValues(doc), isSimpleType, javaType);
                }
            }
        } else {
            if (isSimpleType) {
                String value = "";
                if (rootElement.getChildNodes().getLength() > 0) {
                    value = rootElement.getChildNodes().item(0).getTextContent();
                }
                return new ObjectFactoryImpl(property, value, isSimpleType, javaType);
            } else {
                List<Node> nodes = getComplexPropertyValues(doc);
                Object value = null;
                if (!nodes.isEmpty()) {
                    value = nodes.get(0);
                }
                return new ObjectFactoryImpl(property, value, isSimpleType, javaType);
            }

        }
    }

    public ObjectFactory createValueFactory(Property property, Object propertyValue, Class javaType) {
        isSimpleType = isSimpleType(property);
        Document doc = (Document)propertyValue;
        Element rootElement = doc.getDocumentElement();
        if (property.isMany()) {
            if (isSimpleType) {
                String value = "";
                if (rootElement.getChildNodes().getLength() > 0) {
                    value = rootElement.getChildNodes().item(0).getTextContent();
                }
                List<String> values = getSimplePropertyValues(value, javaType);
                return new ListObjectFactoryImpl(property, values, isSimpleType, javaType);
            } else {
                return new ListObjectFactoryImpl(property, getComplexPropertyValues(doc), isSimpleType, javaType);
            }
        } else {
            if (isSimpleType) {
                String value = "";
                if (rootElement.getChildNodes().getLength() > 0) {
                    value = rootElement.getChildNodes().item(0).getTextContent();
                }
                return new ObjectFactoryImpl(property, value, isSimpleType, javaType);
            } else {
                List<Node> nodes = getComplexPropertyValues(doc);
                Object value = null;
                if (!nodes.isEmpty()) {
                    value = nodes.get(0);
                }
                return new ObjectFactoryImpl(property, value, isSimpleType, javaType);
            }

        }
    } 

    private boolean isSimpleType(Property property) {
        if (property.getXSDType() != null) {
            return SimpleTypeMapperImpl.isSimpleXSDType(property.getXSDType());
        } else {
            if (property instanceof Document) {
                Document doc = (Document)property;
                Element element = doc.getDocumentElement();
                if (element.getChildNodes().getLength() == 1 && element.getChildNodes().item(0).getNodeType() == Element.TEXT_NODE) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<String> getSimplePropertyValues(String concatenatedValue, Class javaType) {
        List<String> propValues = new ArrayList<String>();
        StringTokenizer st = null;
        if (javaType.getName().equals("java.lang.String")) {
            st = new StringTokenizer(concatenatedValue, "\"");
        } else {
            st = new StringTokenizer(concatenatedValue);
        }
        String aToken = null;
        while (st.hasMoreTokens()) {
            aToken = st.nextToken();
            if (aToken.trim().length() > 0) {
                propValues.add(aToken);
            }
        }
        return propValues;
    }

    private List<Node> getComplexPropertyValues(Document document) {
        Element rootElement = document.getDocumentElement();
        List<Node> propValues = new ArrayList<Node>();
        NodeList nodes = rootElement.getChildNodes();
        for (int count = 0; count < nodes.getLength(); ++count) {
            if (nodes.item(count).getNodeType() == Document.ELEMENT_NODE) {
                propValues.add(DOMHelper.promote(nodes.item(count)));
            }
        }
        return propValues;
    }

    public abstract class ObjectFactoryImplBase implements ObjectFactory {
        protected SimpleTypeMapper simpleTypeMapper = new SimpleTypeMapperImpl();
        protected Property property;
        protected Object propertyValue;
        protected Class javaType;
        protected DataType<XMLType> sourceDataType;
        protected DataType<?> targetDataType;
        boolean isSimpleType;

        public ObjectFactoryImplBase(Property property, Object propertyValue, boolean isSimpleType, Class javaType) {
            this.isSimpleType = isSimpleType;
            this.property = property;
            this.propertyValue = propertyValue;
            this.javaType = javaType;
            sourceDataType = new DataTypeImpl<XMLType>(DOMDataBinding.NAME, Node.class, new XMLType(null, this.property
                .getXSDType()));
            TypeInfo typeInfo = null;
            if (this.property.getXSDType() != null) {
                if (SimpleTypeMapperImpl.isSimpleXSDType(this.property.getXSDType())) {
                    typeInfo = new TypeInfo(property.getXSDType(), true, null);
                } else {
                    typeInfo = new TypeInfo(property.getXSDType(), false, null);
                }
            } else {
                typeInfo = new TypeInfo(property.getXSDType(), false, null);
            }

            XMLType xmlType = new XMLType(typeInfo);
            String dataBinding = null; // (String)property.getExtensions().get(DataBinding.class.getName());
            if (dataBinding != null) {
                targetDataType = new DataTypeImpl<XMLType>(dataBinding, javaType, xmlType);
            } else {
                targetDataType = new DataTypeImpl<XMLType>(dataBinding, javaType, xmlType);
                mediator.getDataBindings().introspectType(targetDataType, null);
            }
        }
    }

    public class ObjectFactoryImpl extends ObjectFactoryImplBase {
        public ObjectFactoryImpl(Property property, Object propertyValue, boolean isSimpleType, Class javaType) {
            super(property, propertyValue, isSimpleType, javaType);
        }

        @SuppressWarnings("unchecked")
        public Object getInstance() throws ObjectCreationException {
            if (isSimpleType) {
                try {
                    return simpleTypeMapper.toJavaObject(property.getXSDType(), (String)propertyValue, null);
                } catch (NumberFormatException ex) {
                    throw new ObjectCreationException("Failed to create instance for property " 
                            + property.getName() + " with value " + propertyValue, ex);
                } catch (IllegalArgumentException ex) {
                    throw new ObjectCreationException("Failed to create instance for property " 
                            + property.getName() + " with value " + propertyValue, ex);
                }
            } else {
                return mediator.mediate(propertyValue, sourceDataType, targetDataType, null);
                // return null;
            }
        }
    }

    public class ListObjectFactoryImpl extends ObjectFactoryImplBase {
        public ListObjectFactoryImpl(Property property, List<?> propertyValues, boolean isSimpleType, Class javaType) {
            super(property, propertyValues, isSimpleType, javaType);
        }

        @SuppressWarnings("unchecked")
        public List<?> getInstance() throws ObjectCreationException {
            if (isSimpleType) {
                List<Object> values = new ArrayList<Object>();
                for (String aValue : (List<String>)propertyValue) {
                    try {
                        values.add(simpleTypeMapper.toJavaObject(property.getXSDType(), aValue, null));
                    } catch (NumberFormatException ex) {
                        throw new ObjectCreationException("Failed to create instance for property " 
                                + property.getName() + " with value " + aValue 
                                + " from value list of " + propertyValue, ex);
                    } catch (IllegalArgumentException ex) {
                        throw new ObjectCreationException("Failed to create instance for property " 
                                + property.getName() + " with value " + aValue 
                                + " from value list of " + propertyValue, ex);
                    }
                }
                return values;
            } else {
                List instances = new ArrayList();
                for (Node aValue : (List<Node>)propertyValue) {
                    instances.add(mediator.mediate(aValue, sourceDataType, targetDataType, null));
                }
                return instances;
            }
        }
    }
    
    public class ArrayObjectFactoryImpl extends ObjectFactoryImplBase {
        public ArrayObjectFactoryImpl(Property property, List<?> propertyValues, boolean isSimpleType, Class javaType) {
            super(property, propertyValues, isSimpleType, javaType);
        }

        @SuppressWarnings("unchecked")
        public Object getInstance() throws ObjectCreationException {
            if (isSimpleType) {
                int count = 0;
                Object values = Array.newInstance(javaType, ((List<Object>)propertyValue).size());
                for (String aValue : (List<String>)propertyValue) {
                    try {
                        Array.set(values, count++, simpleTypeMapper.toJavaObject(property.getXSDType(), aValue, null));
                    } catch (NumberFormatException ex) {
                        throw new ObjectCreationException("Failed to create instance for property " 
                                + property.getName() + " with value " + aValue
                                + " from value list of " + propertyValue, ex);
                    } catch (IllegalArgumentException ex) {
                        throw new ObjectCreationException("Failed to create instance for property " 
                                + property.getName() + " with value " + aValue
                                + " from value list of " + propertyValue, ex);
                    }
                }
                return values;
            } else {
                Object instances = Array.newInstance(javaType, ((List<Object>)propertyValue).size());
                int count = 0;
                for (Node aValue : (List<Node>)propertyValue) {
                    Array.set(instances, count++, mediator.mediate(aValue, sourceDataType, targetDataType, null));
                }
                return instances;
            }
        }
    }

    /**
     * This method will create an instance of the value for the specified Property.
     * 
     * @param property The Property from which to retrieve the property value
     * @param type The type of the property value being retrieved from the Property
     * @param <B> Type type of the property value being looked up
     * 
     * @return the value for the Property
     */
    public <B> B createPropertyValue(ComponentProperty property, Class<B> type)
    {
        ObjectFactory<B> factory = this.createValueFactory(property, property.getValue(), type);
        return factory.getInstance();
    }
}
