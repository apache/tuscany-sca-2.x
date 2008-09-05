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

package org.apache.tuscany.sca.itest;

import java.io.ByteArrayInputStream;

import junit.framework.Assert;
import helloworld.HelloWorldService;

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.apache.ws.security.util.Base64;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class BasicAuthTestCase {
    private static SCANode node;
    private static HelloWorldService service;

    @BeforeClass
    public static void init() throws Exception {
        try {
            SCANodeFactory factory = SCANodeFactory.newInstance();
            node = factory.createSCANodeFromClassLoader("helloworld.composite", 
                                                        BasicAuthTestCase.class.getClassLoader());
            node.start();
            
            service = ((SCAClient)node).getService(HelloWorldService.class, "HelloWorldClientComponent");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @AfterClass
    public static void destroy() throws Exception {
        node.stop();
    }
    
    @Test
    //@Ignore
    public void testViaSCAClient() {
        String greetings = service.getGreetings("Simon");
        System.out.println(">>>" + greetings);
    } 
    
    @Test
    @Ignore
    public void testWSViaNonSCAClient() {
        
        try {
            String token ="MyToken";
            String encToken = Base64.encode(token.getBytes());
            
            String response = callService("http://L3AW203:8085/HelloWorldServiceWSComponent",
                                          "<?xml version='1.0' encoding='UTF-8'?>" +
                                            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" + 
                                             "<soapenv:Header>" + 
                                               "<ns2:Token xmlns:ns2=\"http://helloworld/\">" + encToken + "</ns2:Token>" + 
                                             "</soapenv:Header>" + 
                                             "<soapenv:Body>" + 
                                               "<ns2:getGreetings xmlns:ns2=\"http://helloworld/\">" + 
                                                 "<arg0>Simon</arg0>" + 
                                               "</ns2:getGreetings>" + 
                                             "</soapenv:Body>" + 
                                            "</soapenv:Envelope>" );
            System.out.println(">>>" + response);
        } catch(Exception ex) {
            System.out.println(ex.toString());
        }
    } 
    
    @Test
    public void testJMSViaNonSCAClient() {
        // TODO
    }
    
    public String callService(String url, String requestString) throws Exception {
        System.out.println("Request = " + requestString);
        WebConversation wc   = new WebConversation();
        wc.setAuthorization("Me", "MyPasswd");
        WebRequest request   = new PostMethodWebRequest( url, 
                                                         new ByteArrayInputStream(requestString.getBytes("UTF-8")),"text/xml");
        request.setHeaderField("SOAPAction", "");
        WebResponse response = wc.getResource(request);
        System.out.println("Response= " + response.getText());               
        Assert.assertEquals(200, response.getResponseCode());
        return response.getText(); 
    }    
}
