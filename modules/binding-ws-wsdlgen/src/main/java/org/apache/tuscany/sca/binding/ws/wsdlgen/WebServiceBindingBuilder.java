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

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.builder.BindingBuilder;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.policy.BindingType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySubject;

/**
 * A factory for the calculated WSDL document needed by Web Service bindings.
 * 
 * @version $Rev$ $Date$
 */
public class WebServiceBindingBuilder implements BindingBuilder<WebServiceBinding> {

    private ExtensionPointRegistry extensionPoints;

    public WebServiceBindingBuilder(ExtensionPointRegistry extensionPoints) {
        this.extensionPoints = extensionPoints;
    }

    /**
     * Create a calculated WSDL document and save it in the Web Service binding. 
     */
    public void build(Component component, Contract contract, WebServiceBinding binding, BuilderContext context, boolean rebuild) {
        // in some cases (callback service endpoint processing) we need to re-set the binding interface contract
        // and re-generate the WSDL doc from it. This is because the callback binding may be cloned from the 
        // forward binding
        if (rebuild == true){
            binding.setBindingInterfaceContract(null);
            binding.setGeneratedWSDLDocument(null);
        }
        
        BindingWSDLGenerator.generateWSDL(component, contract, binding, extensionPoints, context.getMonitor());
        
        /*
        * Set the default mayProvides intent provided by the binding. For example, 
        * It mayProvides SOAP.v1_1 and SOAP.v1_2. If you don't specify any intents 
        * it implements SOAP.v1_1 by default and hence the default intent
        * is SOAP.v1_1. Binding.ws doesn't allwaysProvide SOAP.v1_1 though as if the 
        * user specifies the SOAP.v1_2 the binding does SOAP.v1_2 instead of SOAP.v1_1
        */
        boolean addDefaultSOAPIntent = true;
        
        for(Intent intent : ((PolicySubject)binding).getRequiredIntents()){
            if (intent.getName().getLocalPart().equals("SOAP.v1_1")){
                addDefaultSOAPIntent = false;
                break;
            }
            if (intent.getName().getLocalPart().equals("SOAP.v1_2")){
                addDefaultSOAPIntent = false;
                break;
            }
        }
        
        if (addDefaultSOAPIntent){
            Definitions systemDefinitions = context.getDefinitions();
            if (systemDefinitions != null){
                BindingType bindingType = systemDefinitions.getBindingType(binding.getType());
                Intent defaultIntent = null;
                for (Intent intent : bindingType.getMayProvidedIntents()){
                    if (intent.getName().getLocalPart().equals("SOAP.v1_1")){
                        defaultIntent = intent;
                    }
                }
                
                if (defaultIntent != null){
                    ((PolicySubject)binding).getRequiredIntents().add(0, defaultIntent);
                }
            }
        }
        
    }

    public QName getBindingType() {
        return WebServiceBinding.TYPE;
    }

}
