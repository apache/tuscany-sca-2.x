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
package com.tuscanyscatours.smsgateway;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

public class SMSGatewayEJBServiceBootstrap {

    public static void main(String[] args) throws Exception {
        System.out.println("Publishing SMS Gateway Service as an EJB service");

        Properties properties = new Properties();
        properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");
        properties.setProperty("openejb.embedded.remotable", "true");

        InitialContext initialContext = new InitialContext(properties);

        //      Object object = initialContext.lookup("SMSGatewayImplRemote");
        //      SMSGateway smsGateway = (SMSGateway) object;
        //      smsGateway.sendSMS("From", "to", "Message");

        System.out.println("EJB server running - waiting for requests");
        System.out.println("Press enter to shutdown.");
        System.in.read();
    }
}
