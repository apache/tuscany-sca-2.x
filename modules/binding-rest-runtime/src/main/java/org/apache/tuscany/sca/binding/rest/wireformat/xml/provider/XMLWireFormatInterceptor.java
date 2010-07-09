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

package org.apache.tuscany.sca.binding.rest.wireformat.xml.provider;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.binding.rest.RESTBinding;
import org.apache.tuscany.sca.binding.rest.wireformat.json.JSONWireFormat;
import org.apache.tuscany.sca.binding.rest.wireformat.xml.XMLWireFormat;
import org.apache.tuscany.sca.common.http.HTTPContext;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * JSON wire format Interceptor.
 *
 * @version $Rev$ $Date$
*/
public class XMLWireFormatInterceptor implements Interceptor {
    private XMLInputFactory inputFactory;

    private Invoker next;
    private RESTBinding binding;

    public XMLWireFormatInterceptor(ExtensionPointRegistry extensionPoints, RuntimeEndpoint endpoint) {
        FactoryExtensionPoint factories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        inputFactory = factories.getFactory(XMLInputFactory.class);

        this.binding = (RESTBinding) endpoint.getBinding();
    }

    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }

    public Message invoke(Message msg) {
        HTTPContext bindingContext = (HTTPContext) msg.getBindingContext();
        if (bindingContext == null) {
            return getNext().invoke(msg);
        }


        if (binding.getRequestWireFormat() instanceof XMLWireFormat) {
            if( isPayloadSupported(bindingContext.getHttpRequest().getMethod()) && msg.getBody() != null) {
                msg = invokeRequest(bindingContext, msg);
            }
        }

        msg = getNext().invoke(msg);

        //if it's oneway return back
        Operation operation = msg.getOperation();
        if (operation != null && operation.isNonBlocking()) {
            return msg;
        }

        if (binding.getResponseWireFormat() instanceof XMLWireFormat) {
            msg = invokeResponse(bindingContext, msg);
        }

       return msg;
    }

    /**
     * Handle any wire format specific transformations required for request data
     * @param bindingContext the binding context (e.g. HTTP Request, Response objects)
     * @param msg the invocation message
     * @return processed request message
     */
    private Message invokeRequest(HTTPContext bindingContext, Message msg) {
        // Decode using the charset in the request if it exists otherwise
        // use UTF-8 as this is what all browser implementations use.
        String charset = bindingContext.getHttpRequest().getCharacterEncoding();
        if (charset == null) {
            charset = "UTF-8";
        }

        try {
            if(msg.getBody() != null) {
                Object[] args = msg.getBody();
                InputStream data = (InputStream) args[0];
                XMLStreamReader xmlPayload = inputFactory.createXMLStreamReader(data, charset);
                msg.setBody(new Object[]{xmlPayload});
            }
        } catch(Exception e) {
            throw new RuntimeException("Unable to process xml paylod: " + msg.getBody().toString());
        }

        return msg;
    }


    /**
     * Handle any wire format specific transformation required for the response data
     * @param bindingContext the binding context (e.g. HTTP Request, Response objects)
     * @param msg the response message
     * @return processed response message
     */
    private Message invokeResponse(HTTPContext bindingContext, Message msg) {
        return msg;
    }

    /**
     * Check if HTTP Operation should support payload
     * @param operation
     * @return
     */
    private static boolean isPayloadSupported(String operation) {
        boolean isGet = "get".equalsIgnoreCase(operation);
        boolean isDelete = "delete".equalsIgnoreCase(operation);

        return  isGet == false && isDelete == false;
    }
}
