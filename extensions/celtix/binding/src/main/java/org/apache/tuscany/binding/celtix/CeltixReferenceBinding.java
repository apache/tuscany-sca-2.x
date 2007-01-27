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
package org.apache.tuscany.binding.celtix;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.xml.namespace.QName;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ReferenceBindingExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.TargetInvoker;

import commonj.sdo.helper.TypeHelper;
import org.objectweb.celtix.Bus;

/**
 * The implementation of a {@link org.apache.tuscany.spi.component.ReferenceBinding} configured with the Celtix binding
 *
 * @version $Rev$ $Date$
 */
public class CeltixReferenceBinding<T> extends ReferenceBindingExtension {
    private static final QName BINDING_WS = new QName(XML_NAMESPACE_1_0, "binding.ws");

    private Bus bus;
    private Port port;
    private Definition wsdlDef;
    private Service wsdlService;
    private TypeHelper typeHelper;

    public CeltixReferenceBinding(String name,
                                  CompositeComponent parent,
                                  WebServiceBindingDefinition binding,
                                  Bus theBus,
                                  TypeHelper theTypeHelper) {
        super(name, parent);
        this.wsdlDef = binding.getWSDLDefinition();
        this.port = binding.getWSDLPort();
        this.wsdlService = binding.getWSDLService();
        this.bus = theBus;
        this.typeHelper = theTypeHelper;
    }

    public QName getBindingType() {
        return BINDING_WS;
    }

    public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation) {
        try {
            return new CeltixInvoker(operation.getName(), bus, port, wsdlService, wsdlDef, typeHelper);
        } catch (BuilderException e) {
            // fixme
            throw new CeltixServiceInitException(e);
        }
    }
}
