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

package org.apache.tuscany.sca.binding.ws.wsdlgen;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.builder.BindingBuilder;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.ContractBuilder;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ResolverExtension;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.apache.tuscany.sca.xsd.XSDFactory;

/**
 * Created WSDL contracts for Endpoints or EndpointReferences for use during 
 * interface contract mapping. The assmebly spec defines WSDL as the lowest
 * common denominator for contract mapping. 
 */
public class WSDLContractBuilder implements ContractBuilder {

    private ExtensionPointRegistry extensionPoints;
    private FactoryExtensionPoint modelFactories;
    private DataBindingExtensionPoint dataBindings;
    private WSDLFactory wsdlFactory;
    private XSDFactory xsdFactory;
    private DocumentBuilderFactory documentBuilderFactory;

    public WSDLContractBuilder(ExtensionPointRegistry extensionPoints) {
        this.extensionPoints = extensionPoints;
        
        modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        dataBindings = extensionPoints.getExtensionPoint(DataBindingExtensionPoint.class);
        wsdlFactory = modelFactories.getFactory(WSDLFactory.class);
        xsdFactory = modelFactories.getFactory(XSDFactory.class);
        documentBuilderFactory = modelFactories.getFactory(DocumentBuilderFactory.class);

    }   
    
    public boolean build(InterfaceContract interfaceContract, BuilderContext context){
// Uncomment the printWSDL =  lines to see the WSDL that is generated
// for interface matching purposes
//        BindingWSDLGenerator.printWSDL = true;
        JavaInterfaceContract javaContract = (JavaInterfaceContract)interfaceContract;        
        WSDLInterfaceContract wsdlContract = 
            BindingWSDLGenerator.createWSDLInterfaceContract(javaContract, 
                                                             false, 
                                                             null,
                                                             dataBindings, 
                                                             wsdlFactory, 
                                                             xsdFactory, 
                                                             documentBuilderFactory, 
                                                             null);
        javaContract.setNormailizedWSDLContract(wsdlContract);
//        BindingWSDLGenerator.printWSDL = false;
        return true;
    }

}
