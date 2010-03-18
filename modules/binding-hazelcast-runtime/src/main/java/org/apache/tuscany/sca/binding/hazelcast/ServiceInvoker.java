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

package org.apache.tuscany.sca.binding.hazelcast;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.Callable;

import org.apache.tuscany.sca.common.xml.dom.DOMHelper;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.oasisopen.sca.ServiceRuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class ServiceInvoker implements Callable<String>, Serializable {
    private static final long serialVersionUID = 1L;

    // all fields MUST be Serializable
    private String serviceURI;
    private String operationName;
    private String requestXML;

    public ServiceInvoker(String serviceURI, String operationName, String msgXML) {
        this.serviceURI = serviceURI;
        this.operationName = operationName;
        this.requestXML = msgXML;
    }

    public String call() throws Exception {
        RuntimeEndpoint endpoint = EndpointStash.getEndpoint(serviceURI);
        Operation operation = getRequestOperation(endpoint);
        DOMHelper domHelper = DOMHelper.getInstance(endpoint.getCompositeContext().getExtensionPointRegistry());
        Object[] args = getRequestArgs(domHelper);
        Object response = endpoint.invoke(operation, args);
        String responseXML = getResponseXML(domHelper, response);
        return responseXML;
    }

    private Operation getRequestOperation(RuntimeEndpoint endpoint) {
        InterfaceContract ic = endpoint.getBindingInterfaceContract();
        Interface iface = ic.getInterface();
        for (Operation op : iface.getOperations()) {
            if (op.getName().equals(operationName)) {
                return op;
            }

        }
        // TODO: return err msg
        throw new ServiceRuntimeException("operation not found " + operationName);
    }

    private Object[] getRequestArgs(DOMHelper domHelper) throws IOException, SAXException {
        Document requestDOM = domHelper.load(requestXML);
        return new Object[] {requestDOM};
    }

    private String getResponseXML(DOMHelper domHelper, Object response) {
        String responseXML = domHelper.saveAsString((Node)response);
        return responseXML;
    }

}
