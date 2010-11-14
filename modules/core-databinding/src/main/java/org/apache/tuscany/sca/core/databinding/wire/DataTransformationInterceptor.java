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

package org.apache.tuscany.sca.core.databinding.wire;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.Invocable;

/**
 * An interceptor to transform data across databindings on the wire
 * 
 * @version $Rev$ $Date$
 */
public class DataTransformationInterceptor implements Interceptor {
    private Invoker next;

    private Operation sourceOperation;

    private Operation targetOperation;
    private Invocable invocable;
    private Mediator mediator;

    public DataTransformationInterceptor(Invocable invocable,
                                         Operation sourceOperation,
                                         Operation targetOperation,
                                         Mediator mediator) {
        super();
        this.sourceOperation = sourceOperation;
        this.targetOperation = targetOperation;
        if ( sourceOperation instanceof JavaOperation ) {
        	JavaOperation javaOp = (JavaOperation) sourceOperation;
        	Method sourceMethod = javaOp.getJavaMethod();
        }
        
        this.mediator = mediator;
        this.invocable = invocable;
    }

    public Invoker getNext() {
        return next;
    }

    public Message invoke(Message msg) {
        Map<String, Object> metadata = new HashMap<String, Object>();
        metadata.put(Invocable.class.getName(), invocable);
        Object input = mediator.mediateInput(msg.getBody(), sourceOperation, targetOperation, metadata);
        msg.setBody(input);
        Message resultMsg = next.invoke(msg);
       
        if (sourceOperation.isNonBlocking()) {
            // Not to reset the message body
            return resultMsg;
        }

        Object result = resultMsg.getBody();
        
        if (resultMsg.isFault()) {
            Object transformedFault = null;
            if ((result instanceof Exception) && !(result instanceof RuntimeException)) {
                transformedFault = mediator.mediateFault(result, sourceOperation, targetOperation, metadata);
                if (transformedFault != result) {
                    resultMsg.setFaultBody(transformedFault);
                }
            }
            //
            // Leave it to another layer to actually throw the Exception which constitutes
            // the message body.  We don't throw it here.
            //
        } else {
            assert !(result instanceof Throwable) : "Expected messages that are not throwable " + result;
            Object newResult = mediator.mediateOutput(result, sourceOperation, targetOperation, metadata);
            resultMsg.setBody(newResult);
        }

        return resultMsg;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }

    /**
     * Returns return type for first Holder in input list.
     * Returns null if the inputs do not contain a Holder.
     */
    protected  List<DataType<DataType>> getHolderTypes( DataType<List<DataType>> inputTypes ) {
    	ArrayList<DataType<DataType>> returnTypes = new ArrayList<DataType<DataType>>();
    	if (inputTypes != null) {
    		
    		List<DataType> logicalType = inputTypes.getLogical();
    		if (logicalType != null) {
    			for (int i = 0; i < logicalType.size(); i++) {
    				DataType dataType = logicalType.get(i);
    				if (isHolder(dataType.getGenericType())) {
    					returnTypes.add(dataType);
    				}
    			}
    		}
    	}
    	return returnTypes;
    }
    
    protected static boolean isHolder( Type type ) {
    	String typeString = type.toString();
    	if ( typeString.startsWith( "javax.xml.ws.Holder" ) ) {
    		return true;
    	}
    	return false;        
    }
}
