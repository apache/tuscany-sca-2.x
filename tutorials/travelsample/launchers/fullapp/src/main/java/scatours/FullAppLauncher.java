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

import java.io.IOException;

import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;

public class FullAppLauncher {

    public static void main(String[] args) throws Exception {
        SCANode node =
            SCANodeFactory.newInstance().createSCANode(null,
                                                       locate("common"),
                                                       locate("currency"),
                                                       locate("hotel"),
                                                       locate("flight"),
                                                       locate("car"),
                                                       locate("trip"),
                                                       locate("tripbooking"),
                                                       locate("travelcatalog"),
                                                       //locate("payment-java-policy"),
                                                       locate("payment-spring-policy"),
                                                       locate("creditcard-payment-jaxb-policy"),
                                                       locate("shoppingcart"),
                                                       locate("scatours"),
                                                       locate("fullapp-ui"),
                                                       locate("fullapp-coordination"),
                                                       locate("fullapp-currency"),
                                                       locate("fullapp-packagedtrip"),
                                                       locate("fullapp-bespoketrip"),
                                                       locate("fullapp-shoppingcart"));

        node.start();

        System.out.println("Point your browser at - http://localhost:8080/scatours/ ");
        System.out.println("Node started - Press enter to shutdown.");

        try {
            System.in.read();
        } catch (IOException e) {
        }

        node.stop();
    }
}
