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

package org.apache.tuscany.sca.binding.jsonrpc.provider;

import java.math.BigDecimal;
import java.util.List;

import org.apache.tuscany.sca.databinding.javabeans.SimpleJavaDataBinding;
import org.apache.tuscany.sca.databinding.json.JSONDataBinding;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;

/**
 * JSONRPC Binding helper class to handle setting the proper
 * data binding in the interface contract for JSONRPC
 * services and references 
 * 
 * @version $Rev$ $Date$
 */
public class JSONRPCDatabindingHelper {

    @SuppressWarnings("unchecked")
    static void setDataBinding(Interface interfaze) {
        List<Operation> operations = interfaze.getOperations();
        for (Operation operation : operations) {
            operation.setDataBinding(JSONDataBinding.NAME);
            DataType<List<DataType>> inputType = operation.getInputType();
            if (inputType != null) {
                List<DataType> logical = inputType.getLogical();
                for (DataType inArg : logical) {
                    if (!SimpleJavaDataBinding.NAME.equals(inArg.getDataBinding()) ||
                        inArg.getPhysical() == BigDecimal.class) {
                        inArg.setDataBinding(JSONDataBinding.NAME);
                    } 
                }
            }

            for (DataType outputType : operation.getOutputType().getLogical() ) {
            	if (outputType != null) {
            		if (!SimpleJavaDataBinding.NAME.equals(outputType.getDataBinding()) ||
            				outputType.getPhysical() == BigDecimal.class   ) {
            			outputType.setDataBinding(JSONDataBinding.NAME);
            		}
            	}
            }
        }
    }
}
