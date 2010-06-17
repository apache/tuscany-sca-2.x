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
        // in some cases (callback service endpoint processing) we need to re-generate the 
        // WSDL doc from a ws binding that already has one. 
        if (rebuild == true){
            binding.setGeneratedWSDLDocument(null);
        }
        BindingWSDLGenerator.generateWSDL(component, contract, binding, extensionPoints, context.getMonitor());
    }

    public QName getBindingType() {
        return WebServiceBinding.TYPE;
    }

}
