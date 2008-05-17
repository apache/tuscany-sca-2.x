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

package helloworld;

import org.apache.tuscany.implementation.bpel.example.helloworld.HelloPortType;
import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * Simple BPEL sample application invoking a helloworld 
 * 
 * @version $Rev: 613905 $ $Date: 2008-01-21 14:41:15 +0000 (Mon, 21 Jan 2008) $
 */
public class BPELClient {
    public static void main(String[] args) throws Exception {

        SCADomain scaDomain = SCADomain.newInstance("helloworld.composite");
        HelloPortType bpelService = scaDomain.getService(HelloPortType.class, "BPELHelloWorldComponent");
        
        String result = bpelService.hello("Hello");
        System.out.println(result);
        
        scaDomain.close();

        System.exit(0);
    }
}
