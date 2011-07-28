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

package org.apache.tuscany.sca.binding.sca.transform;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;

public class XMLWFBindingSCATransformer implements BindingSCATransformer {

    private Mediator mediator;
    private Operation sourceOperation;
    private Operation wsdlBindingOperation;
    private Operation targetOperation;
    
    public XMLWFBindingSCATransformer(Mediator mediator, Operation sourceOperation, Operation wsdlBindingOperation, InvocationChain chain) {
        this.mediator = mediator;
        this.sourceOperation = sourceOperation;
        this.wsdlBindingOperation = wsdlBindingOperation;
        this.targetOperation = chain.getTargetOperation();
    }
    
    @Override
    public Object transformInput(Object body) {
        Map<String, Object> map1 = new HashMap<String, Object>();      
        Object intermediate = mediator.mediateInput(body, sourceOperation, wsdlBindingOperation, map1);
        Map<String, Object> map2 = new HashMap<String, Object>();
        Object result  = mediator.mediateInput(intermediate, wsdlBindingOperation, targetOperation, map2);
        return result;
    }

    @Override
    public Object transformOutput(Object body) {
        Map<String, Object> map1 = new HashMap<String, Object>();              
        Object intermediate  = mediator.mediateOutput(body, wsdlBindingOperation, targetOperation, map1);
        Map<String, Object> map2 = new HashMap<String, Object>();
        Object result = mediator.mediateOutput(intermediate, sourceOperation, wsdlBindingOperation, map2);
        
        return result;

    }

    @Override
    public Object transformFault(Object body) {
        Map<String, Object> map1 = new HashMap<String, Object>();      
        Object intermediate = mediator.mediateFault(body, wsdlBindingOperation, targetOperation, map1);
        Map<String, Object> map2 = new HashMap<String, Object>();
        Object result  = mediator.mediateFault(intermediate, sourceOperation, wsdlBindingOperation, map2);
        return result;

    }

}

