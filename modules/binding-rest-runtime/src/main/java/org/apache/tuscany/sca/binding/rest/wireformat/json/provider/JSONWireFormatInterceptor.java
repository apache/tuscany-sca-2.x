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

package org.apache.tuscany.sca.binding.rest.wireformat.json.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.apache.tuscany.sca.common.http.HTTPContext;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.json.JSONObject;

/**
 * JSON wire format Interceptor.
 * 
 * @version $Rev$ $Date$
*/
public class JSONWireFormatInterceptor implements Interceptor {
    private Invoker next;

    public JSONWireFormatInterceptor(ExtensionPointRegistry extensionPoints, RuntimeEndpoint endpoint) {

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
        // Decode using the charset in the request if it exists otherwise
        // use UTF-8 as this is what all browser implementations use.
        String charset = bindingContext.getHttpRequest().getCharacterEncoding();
        if (charset == null) {
            charset = "UTF-8";
        }
        
        try {
            if( isPayloadSupported(bindingContext.getHttpRequest().getMethod()) && msg.getBody() != null) {
                Object[] args = msg.getBody();
                InputStream in = (InputStream) args[0];
                String data = read(in, charset);
                JSONObject jsonPayload = new JSONObject(data);
                msg.setBody(new Object[]{jsonPayload});
            }
        } catch(Exception e) {
            throw new RuntimeException("Unable to parse json paylod: " + msg.getBody().toString());
        }
        
        return getNext().invoke(msg);
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
    
    /**
     * Read JSON payload from HTTP Request Body
     * @param in
     * @param charset
     * @return
     * @throws IOException
     */
    private static String read(InputStream in, String charset) throws IOException {
        StringWriter sw = new StringWriter();
        InputStreamReader reader = new InputStreamReader(in, "UTF-8");
        char[] buf = new char[8192];
        while (true) {
            int size = reader.read(buf);
            if (size < 0) {
                break;
            }
            sw.write(buf, 0, size);
        }
        return sw.toString();
    }

}
