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

package org.apache.tuscany.sca.interfacedef.wsdl.introspect;

import javax.wsdl.PortType;

import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.impl.WSDLInterfaceIntrospectorImpl;
import org.apache.ws.commons.schema.XmlSchemaCollection;

/**
 * Introspector for creating WSDLInterface definitions from WSDL PortTypes.
 * @deprecated Please use WSDLInterfaceFactory instead.
 */
public class DefaultWSDLInterfaceIntrospector extends WSDLInterfaceIntrospectorImpl implements WSDLInterfaceIntrospector {
    
    private WSDLFactory wsdlFactory;
    
    public DefaultWSDLInterfaceIntrospector(WSDLFactory wsdlFactory) {
        super(wsdlFactory);
        this.wsdlFactory = wsdlFactory;
    }

    public WSDLInterface introspect(PortType portType, XmlSchemaCollection inlineSchemas, ModelResolver resolver) throws InvalidInterfaceException {
        WSDLInterface wsdlInterface = wsdlFactory.createWSDLInterface();
        super.introspectPortType(wsdlInterface, portType, inlineSchemas, resolver);
        return wsdlInterface;
    }

}
