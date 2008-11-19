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
package org.apache.tuscany.sca.databinding.xml;


import javax.xml.namespace.QName;

import org.apache.tuscany.sca.databinding.WrapperHandler;
import org.apache.tuscany.sca.databinding.impl.BaseDataBinding;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.w3c.dom.Node;

/**
 * DOM DataBinding
 *
 * @version $Rev$ $Date$
 */
public class DOMDataBinding extends BaseDataBinding {
    public static final String NAME = Node.class.getName();

    public static final String ROOT_NAMESPACE = "http://tuscany.apache.org/xmlns/sca/databinding/dom/1.0";
    public static final QName ROOT_ELEMENT = new QName(ROOT_NAMESPACE, "root");

    public DOMDataBinding() {
        super(NAME, Node.class);
    }

    @Override
    public WrapperHandler getWrapperHandler() {
        return new DOMWrapperHandler();
    }

    @Override
    public Object copy(Object source, DataType dataType, Operation operation) {
        if (Node.class.isAssignableFrom(source.getClass())) {
            Node nodeSource = (Node)source;
            return nodeSource.cloneNode(true);
        }
        return super.copy(source, dataType, operation);
    }

    @Override
    public boolean introspect(DataType type, Operation operation) {
        if (Node.class.isAssignableFrom(type.getPhysical())) {
            if (type.getLogical() == null) {
                type.setLogical(new XMLType(ROOT_ELEMENT, null));
            }
            type.setDataBinding(NAME);
            return true;
        }
        return false;
    }
}
