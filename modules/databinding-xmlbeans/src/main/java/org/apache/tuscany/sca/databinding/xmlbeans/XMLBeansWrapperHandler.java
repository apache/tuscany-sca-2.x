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

package org.apache.tuscany.sca.databinding.xmlbeans;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.databinding.WrapperHandler;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.ElementInfo;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;

/**
 * @version $Rev$ $Date$
 */
public class XMLBeansWrapperHandler implements WrapperHandler<XmlObject> {

    /**
     * @see org.apache.tuscany.sca.databinding.WrapperHandler#create(org.apache.tuscany.sca.interfacedef.Operation, boolean)
     */
    public XmlObject create(Operation operation, boolean input) {
        WrapperInfo wrapperInfo = operation.getWrapper();
        ElementInfo element = input ? wrapperInfo.getInputWrapperElement() : wrapperInfo.getOutputWrapperElement();
        return null;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.WrapperHandler#getChildren(java.lang.Object, org.apache.tuscany.sca.interfacedef.Operation, boolean)
     */
    public List getChildren(XmlObject wrapper, Operation operation, boolean input) {
        List<Object> children = new ArrayList<Object>();

        List<ElementInfo> childElements =
            input ? operation.getWrapper().getInputChildElements() : operation.getWrapper().getOutputChildElements();
        for (ElementInfo e : childElements) {
            XmlObject[] objects = wrapper.selectChildren(e.getQName());
            if (objects != null && objects.length == 1) {
                if (objects[0] instanceof SimpleValue) {
                    children.add(((SimpleValue)objects[0]).getObjectValue());
                } else {
                    children.add(objects[0]);
                }
            }
            // FIXME: What should we do for many-value?
        }
        return children;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.WrapperHandler#getWrapperType(org.apache.tuscany.sca.interfacedef.Operation, boolean)
     */
    public DataType getWrapperType(Operation operation, boolean input) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.WrapperHandler#isInstance(java.lang.Object, org.apache.tuscany.sca.interfacedef.Operation, boolean)
     */
    public boolean isInstance(Object wrapper, Operation operation, boolean input) {
        WrapperInfo wrapperInfo = operation.getWrapper();
        ElementInfo element = input ? wrapperInfo.getInputWrapperElement() : wrapperInfo.getOutputWrapperElement();
        return true;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.WrapperHandler#setChildren(java.lang.Object, java.lang.Object[], org.apache.tuscany.sca.interfacedef.Operation, boolean)
     */
    public void setChildren(XmlObject wrapper, Object[] childObjects, Operation operation, boolean input) {
        List<ElementInfo> childElements =
            input ? operation.getWrapper().getInputChildElements() : operation.getWrapper().getOutputChildElements();
        int i = 0;
        for (ElementInfo c : childElements) {
            SchemaProperty property = wrapper.schemaType().getElementProperty(c.getQName());

            String prop = property.getJavaPropertyName();

            Method setter;
            try {
                setter = wrapper.schemaType().getJavaClass().getMethod("set" + prop, property.getType().getJavaClass());
                setter.invoke(wrapper, childObjects[i++]);
            } catch (Throwable e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

}
