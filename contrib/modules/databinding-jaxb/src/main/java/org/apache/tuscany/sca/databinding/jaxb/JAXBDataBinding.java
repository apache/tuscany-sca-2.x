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

package org.apache.tuscany.sca.databinding.jaxb;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.databinding.WrapperHandler;
import org.apache.tuscany.sca.databinding.XMLTypeHelper;
import org.apache.tuscany.sca.databinding.impl.BaseDataBinding;
import org.apache.tuscany.sca.databinding.impl.DOMHelper;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.w3c.dom.Document;

/**
 * JAXB DataBinding
 *
 * @version $Rev$ $Date$
 */
public class JAXBDataBinding extends BaseDataBinding {
    public static final String NAME = JAXBElement.class.getName();

    public static final String ROOT_NAMESPACE = "http://tuscany.apache.org/xmlns/sca/databinding/jaxb/1.0";
    public static final QName ROOT_ELEMENT = new QName(ROOT_NAMESPACE, "root");
    
    private JAXBWrapperHandler wrapperHandler;
    private JAXBTypeHelper xmlTypeHelper;
    
    public JAXBDataBinding() {
        super(NAME, JAXBElement.class);
        this.wrapperHandler = new JAXBWrapperHandler();
        this.xmlTypeHelper = new JAXBTypeHelper();
    }

    @Override
    public boolean introspect(DataType dataType, Operation operation) {
        Class javaType = dataType.getPhysical();
        if (JAXBElement.class.isAssignableFrom(javaType)) {
            Type type = javaType.getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = ((ParameterizedType)type);
                Type rawType = parameterizedType.getRawType();
                if (rawType == JAXBElement.class) {
                    Type actualType = parameterizedType.getActualTypeArguments()[0];
                    if (actualType instanceof Class) {
                        XMLType xmlType = JAXBContextHelper.getXmlTypeName((Class)actualType);
                        dataType.setLogical(xmlType);
                        dataType.setDataBinding(NAME);
                        return true;
                    }
                }
            }
            if (dataType.getLogical() == null) {
                dataType.setLogical(XMLType.UNKNOWN);
            }
            dataType.setDataBinding(NAME);
            return true;
        }

        XMLType xmlType = JAXBContextHelper.getXmlTypeName(javaType);
        if (xmlType == null) {
            return false;
        }
        dataType.setLogical(xmlType);
        dataType.setDataBinding(NAME);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object copy(Object arg, DataType dataType, Operation operation) {
        try {
            boolean isElement = false;
            if (dataType == null) {
                Class cls = arg.getClass();
                if (arg instanceof JAXBElement) {
                    isElement = true;
                    cls = ((JAXBElement)arg).getDeclaredType();
                }
                dataType = new DataTypeImpl<XMLType>(NAME, cls, XMLType.UNKNOWN);
            }
            JAXBContext context = JAXBContextHelper.createJAXBContext(dataType);
            arg = JAXBContextHelper.createJAXBElement(context, dataType, arg);
            Document doc = DOMHelper.newDocument();
            context.createMarshaller().marshal(arg, doc);
            Object value = context.createUnmarshaller().unmarshal(doc, dataType.getPhysical());
            if (isElement && value instanceof JAXBElement) {
                return value;
            }
            return JAXBContextHelper.createReturnValue(context, dataType, value);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public WrapperHandler getWrapperHandler() {
        return wrapperHandler;
    }

    @Override
    public XMLTypeHelper getXMLTypeHelper() {
        // return new JAXBTypeHelper();
        return xmlTypeHelper;
    }

}
