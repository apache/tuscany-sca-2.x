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

package calculator;

import org.apache.tuscany.host.embedded.SCARuntime;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;

/**
 * @version $Rev: 529177 $ $Date: 2007-04-16 14:34:39 +0530 (Mon, 16 Apr 2007) $
 */
public class CalculatorServer {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting the SCA Calculator Application exposed as RMI Services...");
        SCARuntime.start("CalculatorRMIServer.composite");
        System.out.println("... Press Enter to Exit...");
        System.in.read();
        SCARuntime.stop();
        System.out.println("Exited...");
        System.exit(0);
    }

}
