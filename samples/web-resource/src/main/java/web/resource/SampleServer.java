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
package web.resource;

import java.io.IOException;

import org.apache.tuscany.sca.host.embedded.SCADomain;

public class SampleServer {
    public static void main(String[] args) throws Exception {

        SCADomain scaDomain = SCADomain.newInstance("WebResource.composite");

        try {
            System.out.println("Sample server started (press enter to shutdown)");
            System.out.println();
            System.out.println("To get the Web resource, point your Web browser to the following address:");
            System.out.println("http://localhost:8083/myWeb/index.html");
            System.out.println();
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        scaDomain.close();
        System.out.println("Sample server stopped");
    }
}
