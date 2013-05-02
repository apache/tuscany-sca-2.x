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

package test;

import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.impl.NodeImpl;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLObject;
import org.apache.tuscany.sca.monitor.ValidationException;

public class EndpointHelper {

    public static Endpoint createWSEndpoint(String endpointName, QName wsdlPortQN, String targetURL, String curi, Node node) throws ContributionReadException, ValidationException, InvalidInterfaceException {
        ExtensionPointRegistry extensionPoints = ((NodeImpl)node).getExtensionPointRegistry();
        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);

        Component component = assemblyFactory.createComponent();
        component.setName("foo");        
        
        WebServiceBindingFactory webServiceBindingFactory = modelFactories.getFactory(WebServiceBindingFactory.class);
        WebServiceBinding wsBinding = webServiceBindingFactory.createWebServiceBinding();
        wsBinding.setURI(targetURL);
        
        WSDLFactory wif = modelFactories.getFactory(WSDLFactory.class);

        Contribution c = node.getContribution(curi);
        WSDLDefinition wd = wif.createWSDLDefinition();
        wd.setUnresolved(true);
        wd.setNamespace(wsdlPortQN.getNamespaceURI());
        wd.setNameOfPortTypeToResolve(wsdlPortQN);
        ProcessorContext ctx = new ProcessorContext();
        wd = c.getModelResolver().resolveModel(WSDLDefinition.class, wd, ctx);
        WSDLObject<PortType> pt = wd.getWSDLObject(PortType.class, wsdlPortQN);
        
        WSDLInterface nwi = wif.createWSDLInterface(pt.getElement(), wd, c.getModelResolver(), null);
        nwi.setWsdlDefinition(wd);

        WSDLInterfaceContract wsdlIC = wif.createWSDLInterfaceContract();
        wsdlIC.setInterface(nwi);
        
        wsBinding.setBindingInterfaceContract(wsdlIC);

        wsBinding.setGeneratedWSDLDocument(((WSDLInterface)wsdlIC.getInterface()).getWsdlDefinition().getDefinition());
        wsBinding.setService((Service)wsBinding.getGeneratedWSDLDocument().getServices().values().iterator().next());
        
        Endpoint newEndpoint = assemblyFactory.createEndpoint();
        newEndpoint.setComponent(component);
        ComponentService cs = assemblyFactory.createComponentService();
        cs.setName("baa");
        cs.setInterfaceContract(wsdlIC);
        newEndpoint.setService(cs);
        newEndpoint.setBinding(wsBinding);
        newEndpoint.setURI(endpointName);
        newEndpoint.setRemote(true);
        return newEndpoint;
    }
     
}
