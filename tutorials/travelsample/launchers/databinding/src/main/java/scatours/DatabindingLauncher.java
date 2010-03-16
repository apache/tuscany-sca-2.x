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

package scatours;

import static scatours.launcher.LauncherUtil.locate;

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;

public class DatabindingLauncher {
    public static void main(String[] args) throws Exception {
        SCANode node1 = SCANodeFactory.newInstance().createSCANode(null, 
                                                                   locate("payment-java"), 
                                                                   locate("databinding-client"));

        SCANode node2 = SCANodeFactory.newInstance().createSCANode(null, locate("creditcard-payment-sdo"));

        node1.start();
        node2.start();

        Runnable runner = ((SCAClient)node1).getService(Runnable.class, "TestClient/Runnable");
        runner.run();

        node1.stop();
        node2.stop();
    }
}
