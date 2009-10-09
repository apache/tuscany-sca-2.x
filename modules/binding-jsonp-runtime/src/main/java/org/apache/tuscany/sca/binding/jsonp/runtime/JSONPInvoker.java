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

package org.apache.tuscany.sca.binding.jsonp.runtime;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.DataExchangeSemantics;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class JSONPInvoker implements Invoker, DataExchangeSemantics {
    
    protected Operation operation;
    protected EndpointReference endpoint;

    protected ObjectMapper mapper; // TODO: share mapper btw invoker and servlet or move to databinding

    public JSONPInvoker(Operation operation, EndpointReference endpoint) {
        this.operation = operation;
        this.endpoint = endpoint;
        this.mapper = new ObjectMapper();
    }

    public Message invoke(Message msg) {
        try {

            return doInvoke(msg);
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Message doInvoke(Message msg) throws JsonGenerationException, JsonMappingException, IOException, EncoderException {
        String uri = endpoint.getBinding().getURI() + "/" + operation.getName();
        String[] jsonArgs = objectsToJSON((Object[])msg.getBody());

        String responseJSON = invokeHTTPRequest(uri, jsonArgs);

        Object response = jsonToObjects(responseJSON)[0];
        msg.setBody(response);

        return msg;
    }

    protected String invokeHTTPRequest(String url, String[] jsonArgs) throws IOException, EncoderException {
        
         HttpClient httpclient = new DefaultHttpClient();
         
         
         URLCodec uc = new URLCodec();
         for (int i=0 ; i<jsonArgs.length; i++) {
             if (i == 0) {
                 url += '?';
             } else {
                 url += '&';
             }
             url += "arg" + i + "=";
             url += uc.encode(jsonArgs[i]);
         }

         HttpGet httpget = new HttpGet(url); 

         HttpResponse response = httpclient.execute(httpget);
         
         StringBuffer responseJSON = new StringBuffer(); 

         HttpEntity entity = response.getEntity();
         
         // If the response does not enclose an entity, there is no need
         // to worry about connection release
         if (entity != null) {
             InputStream instream = entity.getContent();
             try {
                 
                 BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
                 String s = null;
                 while ((s = reader.readLine()) != null) {
                     responseJSON.append(s);
                 }
                 
             } catch (IOException ex) {
         
                 // In case of an IOException the connection will be released
                 // back to the connection manager automatically
                 throw ex;
                 
             } catch (RuntimeException ex) {
         
                 // In case of an unexpected exception you may want to abort
                 // the HTTP request in order to shut down the underlying 
                 // connection and release it back to the connection manager.
                 httpget.abort();
                 throw ex;
                 
             } finally {
         
                 // Closing the input stream will trigger connection release
                 instream.close();
                 
             }
             
             // When HttpClient instance is no longer needed, 
             // shut down the connection manager to ensure
             // immediate deallocation of all system resources
             httpclient.getConnectionManager().shutdown();        
         }
         
         return responseJSON.toString();
    }

    protected String[] objectsToJSON(Object[] msgArgs) throws JsonGenerationException, JsonMappingException, IOException {
        String[] jsonArgs = new String[msgArgs.length];
        for (int i=0; i<msgArgs.length; i++) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            mapper.writeValue(os , msgArgs[i]);
            jsonArgs[i] = os.toString();
        }
        return jsonArgs;
    }

    protected Object[] jsonToObjects(String jsonRequest) throws JsonParseException, JsonMappingException, IOException {
        Class<?> c = new Object[0].getClass();
        Object[] args = (Object[])mapper.readValue("[" + jsonRequest +"]", c);
        return args;
    }
    
    public boolean allowsPassByReference() {
        return true;
    }
}
