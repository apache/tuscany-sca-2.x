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
import org.apache.tuscany.sca.common.xml.dom.DOMHelper;
import org.apache.tuscany.sca.context.PropertyValueFactory;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.core.factory.ObjectFactory;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.databinding.SimpleTypeMapper;
import org.apache.tuscany.sca.databinding.impl.SimpleTypeMapperImpl;
import org.apache.tuscany.sca.databinding.xml.DOMDataBinding;
import org.apache.tuscany.sca.implementation.java.JavaElementImpl;
import org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper;
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
    private SimpleTypeMapper simpleTypeMapper;
    private boolean isSimpleType;

    public JavaPropertyValueObjectFactory(ExtensionPointRegistry registry) {
        UtilityExtensionPoint utilityExtensionPoint = registry.getExtensionPoint(UtilityExtensionPoint.class);
        this.mediator = utilityExtensionPoint.getUtility(Mediator.class);
        this.simpleTypeMapper = utilityExtensionPoint.getUtility(SimpleTypeMapper.class);
    }

    public JavaPropertyValueObjectFactory(Mediator mediator) {
        this.mediator = mediator;
    }

    public ObjectFactory createValueFactory(Property property, Object propertyValue, JavaElementImpl javaElement) {
        Document doc = (Document)propertyValue;
        List<Node> nodes = getValues(doc);
        Class<?> javaType = JavaIntrospectionHelper.getBaseType(javaElement.getType(), javaElement.getGenericType());
        if (property.isMany()) {
            if (javaElement.getType().isArray()) {
                return new ArrayObjectFactoryImpl(property, nodes, javaType);
            } else {
                return new ListObjectFactoryImpl(property, nodes, javaType);
            }
        } else {
            Object value = null;
            if (!nodes.isEmpty()) {
                value = nodes.get(0);
            }
            return new ObjectFactoryImpl(property, value, javaType);

        }
    }

    public ObjectFactory createValueFactory(Property property, Object propertyValue, Class<?> javaType) {
        Document doc = (Document)propertyValue;
        List<Node> nodes = getValues(doc);
        if (property.isMany()) {
            return new ListObjectFactoryImpl(property, nodes, javaType);
        } else {
            Object value = null;
            if (!nodes.isEmpty()) {
                value = nodes.get(0);
            }
            return new ObjectFactoryImpl(property, value, javaType);
        }
    }

    public <B> B createPropertyValue(ComponentProperty property, Class<B> type) {
    	
    	validateTypes(property, type);
    	
        ObjectFactory<B> factory = this.createValueFactory(property, property.getValue(), type);
        return factory.getInstance();
    }

	private <B> void validateTypes(ComponentProperty property, Class<B> type) {
		// JAXB seems to do some strange things with conversions, so
    	// we can't rely on the databinding conversion from Node->Java to catch
		// incompatible types. 
		
		DataType prop1 = property.getProperty().getDataType();
    	
		if ( (prop1 != null) && (type.isAssignableFrom(prop1.getPhysical())) )  {
			return;    			
		} else if ( simpleTypeMapper.getXMLType(type) != null ) { 
    		if ( simpleTypeMapper.getXMLType(type).getQName().equals(property.getXSDType())) 
    			return;    			    		
    	} else if ( isSimpleType(property) ) {
    		if ( type.isAssignableFrom(simpleTypeMapper.getJavaType(property.getXSDType()))) 
    			return;    		
    	} 
    	
		throw new IllegalArgumentException("Property type " + prop1.getPhysical().getName() + " is not compatible with " + type.getName());
 
	}

    abstract class ObjectFactoryImplBase implements ObjectFactory {
        protected SimpleTypeMapper simpleTypeMapper = new SimpleTypeMapperImpl();
        protected Property property;
        protected Object propertyValue;
        protected Class<?> javaType;
        protected DataType<XMLType> sourceDataType;
        protected DataType<?> targetDataType;

        public ObjectFactoryImplBase(Property property, Object propertyValue, Class<?> javaType) {
            this.property = property;
            this.propertyValue = propertyValue;
            this.javaType = javaType;
            sourceDataType =
                new DataTypeImpl<XMLType>(DOMDataBinding.NAME, Node.class,
                                          new XMLType(null, this.property.getXSDType()));
            
            targetDataType = property.getDataType();
            if (targetDataType == null) {
                TypeInfo typeInfo = null;
                if (this.property.getXSDType() != null) {
                    if (simpleTypeMapper.isSimpleXSDType(this.property.getXSDType())) {
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
    }

    class ObjectFactoryImpl extends ObjectFactoryImplBase {
        public ObjectFactoryImpl(Property property, Object propertyValue, Class<?> javaType) {
            super(property, propertyValue, javaType);
        }

        public Object getInstance() throws ObjectCreationException {
            if (isSimpleType) {
                try {
                    return simpleTypeMapper.toJavaObject(property.getXSDType(), (String)propertyValue, null);
                } catch (NumberFormatException ex) {
                    throw new ObjectCreationException("Failed to create instance for property " + property.getName()
                        + " with value "
                        + propertyValue, ex);
                } catch (IllegalArgumentException ex) {
                    throw new ObjectCreationException("Failed to create instance for property " + property.getName()
                        + " with value "
                        + propertyValue, ex);
                }
            } else {
                return mediator.mediate(propertyValue, sourceDataType, targetDataType, null);
                // return null;
            }
        }
    }

    class ListObjectFactoryImpl extends ObjectFactoryImplBase {
        public ListObjectFactoryImpl(Property property, List<?> propertyValues, Class<?> javaType) {
            super(property, propertyValues, javaType);
        }

        @SuppressWarnings("unchecked")
        public List<?> getInstance() throws ObjectCreationException {
            if (isSimpleType) {
                List<Object> values = new ArrayList<Object>();
                for (String aValue : (List<String>)propertyValue) {
                    try {
                        values.add(simpleTypeMapper.toJavaObject(property.getXSDType(), aValue, null));
                    } catch (NumberFormatException ex) {
                        throw new ObjectCreationException("Failed to create instance for property " + property
                            .getName()
                            + " with value "
                            + aValue
                            + " from value list of "
                            + propertyValue, ex);
                    } catch (IllegalArgumentException ex) {
                        throw new ObjectCreationException("Failed to create instance for property " + property
                            .getName()
                            + " with value "
                            + aValue
                            + " from value list of "
                            + propertyValue, ex);
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

    class ArrayObjectFactoryImpl extends ObjectFactoryImplBase {
        public ArrayObjectFactoryImpl(Property property, List<?> propertyValues, Class<?> javaType) {
            super(property, propertyValues, javaType);
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
                        throw new ObjectCreationException("Failed to create instance for property " + property
                            .getName()
                            + " with value "
                            + aValue
                            + " from value list of "
                            + propertyValue, ex);
                    } catch (IllegalArgumentException ex) {
                        throw new ObjectCreationException("Failed to create instance for property " + property
                            .getName()
                            + " with value "
                            + aValue
                            + " from value list of "
                            + propertyValue, ex);
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
     * Utility methods
     */

    /**
     *
     * @param property
     * @return
     */
    private boolean isSimpleType(Property property) {
        if (property.getXSDType() != null) {
            return simpleTypeMapper.isSimpleXSDType(property.getXSDType());
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

    /**
     * Retrieve list of simple property values
     * @param concatenatedValue
     * @param javaType
     * @return
     */
    private static List<String> getSimplePropertyValues(String concatenatedValue, Class<?> javaType) {
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

    /**
     * Retrieve the list of complex property values
     * @param document
     * @return
     */
    private static List<Node> getValues(Document document) {
    	 List<Node> propValues = new ArrayList<Node>();
    	 if ( document == null )
    		 return propValues;
        // The root is the property element
        Element rootElement = document.getDocumentElement();
       
        NodeList nodes = rootElement.getChildNodes();
        for (int count = 0; count < nodes.getLength(); ++count) {
            Node node = nodes.item(count);
            if (node.getNodeType() == Document.ELEMENT_NODE) {
                propValues.add(DOMHelper.promote(node));
            }
        }
        return propValues;
    }
}
