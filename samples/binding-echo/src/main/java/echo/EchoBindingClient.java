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
package echo;

import org.apache.tuscany.sca.host.embedded.SCADomain;

import echo.server.EchoServer;

/**
 * This client program shows how to create an SCA runtime, start it,
 * and locate and invoke a SCA component
 * @version $Rev$ $Date$
 */
public class EchoBindingClient {
    
    public static void main(String[] args) throws Exception {

        SCADomain scaDomain  = SCADomain.newInstance("EchoBinding.composite");
        
        // Call the echo service component which will, in turn, call a reference
        // with an echo binding. The echo binding will echo the given string.
        Echo service = scaDomain.getService(Echo.class, "EchoComponent");
        String echoString = service.echo("foo");
        System.out.println("Echo reference = " + echoString );

        // Call the echo server. This will dispatch the call to a service with an 
        // echo binding. The echo binding will pass the call to the echo component.
        echoString = EchoServer.getServer().sendReceive("http://tempuri.org", "bar");
        System.out.println("Echo service = " + echoString );
        
        scaDomain.close();

    }

}
